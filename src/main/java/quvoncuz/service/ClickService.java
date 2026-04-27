package quvoncuz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.click.ClickResponse;
import quvoncuz.dto.click.CompleteRequest;
import quvoncuz.dto.click.PrepareRequest;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentResponse;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.ClickTransactionEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.ClickErrorCode;
import quvoncuz.enums.ClickTransactionStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.ClickTransactionRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.util.ClickSignUtil;
import quvoncuz.util.SecurityUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClickService {

    @Value("${click.service-id}")
    private String serviceId;

    @Value("${click.merchant-id}")
    private String merchantId;

    @Value("${click.base-url}")
    private String baseUrl;

    @Value("${click.secret-key}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ClickTransactionRepository clickTransactionRepository;

    public PaymentResponse generatePaymentUrl(PaymentRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        PaymentEntity payment = paymentRepository
                .findByUserIdAndTourIdAndBookingIdAndStatusIs(userId, request.getTourId(), request.getBookingId(), PaymentStatus.PENDING)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        String encodedReturnUrl = URLEncoder.encode(request.getReturnUrl(), StandardCharsets.UTF_8);
        String url = String.format("%s?service_id=%s&merchant_id=%s&amount=%s&transaction_param=%s&return_url=%s",
                baseUrl, serviceId, merchantId, payment.getAmount(), payment.getId(), encodedReturnUrl);

        ClickTransactionEntity save = clickTransactionRepository.save(
                ClickTransactionEntity.builder()
                        .merchantTransId(payment.getId().toString())
                        .amount(Double.parseDouble(payment.getAmount().toString()))
                        .status(ClickTransactionStatus.CREATED)
                        .userId(userId)
                        .build()
        );

        log.info("Transaction created: id={}, merchantTransId={}, amount={}",
                save.getId(), save.getMerchantTransId(), save.getAmount());
        return PaymentResponse.builder()
                .url(url)
                .build();
    }

    @Transactional
    public ClickResponse prepare(PrepareRequest request) {

        log.info(">>> PREPARE REQUEST: trans={} service={} merchant={} amount=[{}] action={} time=[{}] sign=[{}]",
                request.getClickTransId(),
                request.getServiceId(),
                request.getMerchantTransId(),
                request.getAmount(),
                request.getAction(),
                request.getSignTime(),
                request.getSignString());

        String expectedSign = ClickSignUtil.generatePrepareSign(
                request.getClickTransId(),
                request.getServiceId(),
                secretKey,
                request.getMerchantTransId(),
                request.getAmount(),
                request.getAction(),
                request.getSignTime()
        );

        if (!expectedSign.equals(request.getSignString())) {
            log.error("Signature failed in Prepare-method: {}", request.getSignString());
            return buildError(ClickErrorCode.SIGN_CHECK_FAILED, request.getMerchantTransId(), null, request.getClickTransId());
        }

        if (request.getAction() != 0) {
            log.error("Action failed in Prepare methods. Expected: 0, result: {}", request.getAction());
            return buildError(ClickErrorCode.ACTION_NOT_FOUND, request.getMerchantTransId(), null, request.getClickTransId());
        }

        ClickTransactionEntity transaction = new ClickTransactionEntity();


        Optional<ClickTransactionEntity> optionalTransaction = clickTransactionRepository.findFirstByMerchantTransIdOrderByCreatedAtDesc(request.getMerchantTransId());
        if (optionalTransaction.isPresent()) {
            transaction = optionalTransaction.get();
            if (transaction.getStatus() == ClickTransactionStatus.PAID) {
                log.error("'Transaction-Already-Paid' error: id={}", transaction.getId());
                return buildError(ClickErrorCode.ALREADY_PAID, request.getMerchantTransId(), null, request.getClickTransId());
            }

            if (transaction.getStatus() == ClickTransactionStatus.CANCELLED) {
                log.error("Transaction CANCELLED error in Prepare: id={}", transaction.getId());
                return buildError(ClickErrorCode.TRANSACTION_CANCELLED, request.getMerchantTransId(), null, request.getClickTransId());
            }

            if (!transaction.getAmount().equals(Double.parseDouble(request.getAmount()))) {
                log.error("'Transaction-Amount-Incorrect' error in Prepare-method expected {} and result {}", transaction.getAmount(), request.getAmount());
                return buildError(ClickErrorCode.INCORRECT_AMOUNT, request.getMerchantTransId(), null, request.getClickTransId());
            }

            transaction.setClickTransId(request.getClickTransId());
            transaction.setClickPaydocId(request.getClickPaydocId());
            transaction.setAmount(Double.parseDouble(request.getAmount()));
            transaction.setStatus(ClickTransactionStatus.PREPARED);
            transaction.setSignString(request.getSignString());
            transaction.setSignTime(LocalDateTime.parse(request.getSignTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            transaction = clickTransactionRepository.save(transaction);

            log.info("Transaction saved in PREPARED status: {}", transaction.getId());

        } else {
            throw new NotFoundException("Merchant id not found!");
        }


        ClickResponse clickResponse = buildSuccess(transaction.getClickTransId(), transaction.getMerchantTransId(), transaction.getId().intValue());

        log.info("SUCCESS RESPONSE in Prepare-method: {}", transaction.getId());
        return clickResponse;
    }

    @Transactional
    public ClickResponse complete(CompleteRequest request) {

        log.info(">>> COMPLETE REQUEST: trans={} service={} merchant={} amount=[{}] action={} time=[{}] sign=[{}]",
                request.getClickTransId(),
                request.getServiceId(),
                request.getMerchantTransId(),
                request.getAmount(),
                request.getAction(),
                request.getSignTime(),
                request.getSignString());

        String expectedSign = ClickSignUtil.generateCompleteSign(
                request.getClickTransId(),
                request.getServiceId(),
                secretKey,
                request.getMerchantTransId(),
                Long.valueOf(request.getMerchantPrepareId()),
                request.getAmount(),
                request.getAction(),
                request.getSignTime()
        );

        if (!expectedSign.equals(request.getSignString())) {
            log.error("Signature failed in Complete method {}", request.getSignString());
            return buildError(ClickErrorCode.SIGN_CHECK_FAILED, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        if (request.getAction() != 1) {
            log.error("Action failed in Complete method {}", request.getAction());
            return buildError(ClickErrorCode.ACTION_NOT_FOUND, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        Optional<ClickTransactionEntity> optionalTransaction = clickTransactionRepository.findByIdAndMerchantTransId(
                request.getMerchantPrepareId().longValue(), request.getMerchantTransId());

        if (optionalTransaction.isEmpty()) {
            log.error("Transaction not exists error in Complete method {}", request);
            return buildError(ClickErrorCode.TRANSACTION_NOT_EXIST, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        ClickTransactionEntity transaction = optionalTransaction.get();

        if (transaction.getStatus() == ClickTransactionStatus.PAID) {
            log.error("Transaction already paid error in Complete method {}", request);
            return buildError(ClickErrorCode.ALREADY_PAID, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        if (transaction.getStatus() == ClickTransactionStatus.CANCELLED) {
            log.error("Transaction canceled error in Complete method {}", request);
            return buildError(ClickErrorCode.TRANSACTION_CANCELLED, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        if (!transaction.getAmount().equals(Double.parseDouble(request.getAmount()))) {
            transaction.setStatus(ClickTransactionStatus.CANCELLED);
            ClickTransactionEntity cancelledTransaction = clickTransactionRepository.save(transaction);
            log.info("Transaction cancelled: {}", cancelledTransaction);
            return buildError(ClickErrorCode.INCORRECT_AMOUNT, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        try {

            log.info("transaction begun at status");
            transaction.setStatus(ClickTransactionStatus.PAID);
            log.info("transaction begun at save");
            transaction = clickTransactionRepository.save(transaction);

            log.info("Transaction paid: {}", transaction.getId());
            PaymentEntity payment = paymentRepository.findById(Long.parseLong(transaction.getMerchantTransId()))
                    .orElseThrow(() -> new NotFoundException("Bill not found"));
            payment.setStatus(PaymentStatus.PAID);

            BookingEntity booking = bookingRepository.findById(payment.getBookingId())
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
            booking.setStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);
            paymentRepository.save(payment);
            log.info("Bill turned paid status!");
        } catch (Exception e) {
            log.error(transaction.toString());
            return buildError(ClickErrorCode.FAILED_TO_UPDATE_USER, request.getMerchantTransId(), request.getMerchantPrepareId(), request.getClickTransId());
        }

        ClickResponse clickResponse = buildSuccess(request.getClickTransId(), request.getMerchantTransId(), transaction.getId().intValue());
        log.info("SUCCESS RESPONSE in Complete-method: {}", clickResponse);
        return clickResponse;
    }

    private ClickResponse buildError(ClickErrorCode code,
                                     String merchantTransId,
                                     Integer merchantPrepareId,
                                     Long clickTransId) {
        return ClickResponse.builder()
                .clickTransId(clickTransId)
                .merchantTransId(merchantTransId)
                .merchantPrepareId(merchantPrepareId)
                .error(code.getCode())
                .errorNote(code.getNote())
                .build();
    }

    private ClickResponse buildSuccess(Long clickTransId, String merchantTransId, Integer merchantPrepareId) {
        return ClickResponse.builder()
                .clickTransId(clickTransId)
                .merchantTransId(merchantTransId)
                .merchantPrepareId(merchantPrepareId)
                .error(ClickErrorCode.SUCCESS.getCode())
                .errorNote(ClickErrorCode.SUCCESS.getNote())
                .build();
    }

}
