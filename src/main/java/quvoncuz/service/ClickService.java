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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
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

    public PaymentResponse generatePaymentUrl(PaymentRequestDTO request, Long userId) {
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
                request.getClick_trans_id(),
                request.getService_id(),
                request.getMerchant_trans_id(),
                request.getAmount(),
                request.getAction(),
                request.getSign_time(),
                request.getSign_string());

        String expectedSign = ClickSignUtil.generatePrepareSign(
                request.getClick_trans_id(),
                request.getService_id(),
                secretKey,
                request.getMerchant_trans_id(),
                request.getAmount(),
                request.getAction(),
                request.getSign_time()
        );

        if (!expectedSign.equals(request.getSign_string())) {
            log.error("Signature failed in Prepare-method: {}", request.getSign_string());
            return buildError(ClickErrorCode.SIGN_CHECK_FAILED, request.getMerchant_trans_id(), null, request.getClick_trans_id());
        }

        if (request.getAction() != 0) {
            log.error("Action failed in Prepare methods. Expected: 0, result: {}", request.getAction());
            return buildError(ClickErrorCode.ACTION_NOT_FOUND, request.getMerchant_trans_id(), null, request.getClick_trans_id());
        }

        ClickTransactionEntity transaction = new ClickTransactionEntity();


        Optional<ClickTransactionEntity> optionalTransaction = clickTransactionRepository.findFirstByMerchantTransId(request.getMerchant_trans_id());
        if (optionalTransaction.isPresent()) {
            transaction = optionalTransaction.get();
            if (transaction.getStatus() == ClickTransactionStatus.PAID) {
                log.error("'Transaction-Already-Paid' error: id={}", transaction.getId());
                return buildError(ClickErrorCode.ALREADY_PAID, request.getMerchant_trans_id(), null, request.getClick_trans_id());
            }

            if (transaction.getStatus() == ClickTransactionStatus.CANCELLED) {
                log.error("Transaction CANCELLED error in Prepare: id={}", transaction.getId());
                return buildError(ClickErrorCode.TRANSACTION_CANCELLED, request.getMerchant_trans_id(), null, request.getClick_trans_id());
            }

            if (!transaction.getAmount().equals(Double.parseDouble(request.getAmount()))) {
                log.error("'Transaction-Amount-Incorrect' error in Prepare-method expected {} and result {}", transaction.getAmount(), request.getAmount());
                return buildError(ClickErrorCode.INCORRECT_AMOUNT, request.getMerchant_trans_id(), null, request.getClick_trans_id());
            }

            transaction.setClickTransId(request.getClick_trans_id());
            transaction.setClickPaydocId(request.getClick_paydoc_id());
            transaction.setAmount(Double.parseDouble(request.getAmount()));
            transaction.setStatus(ClickTransactionStatus.PREPARED);
            transaction.setSignString(request.getSign_string());
            transaction.setSignTime(LocalDateTime.parse(request.getSign_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
                request.getClick_trans_id(),
                request.getService_id(),
                request.getMerchant_trans_id(),
                request.getAmount(),
                request.getAction(),
                request.getSign_time(),
                request.getSign_string());

        String expectedSign = ClickSignUtil.generateCompleteSign(
                request.getClick_trans_id(),
                request.getService_id(),
                secretKey,
                request.getMerchant_trans_id(),
                Long.valueOf(request.getMerchant_prepare_id()),
                request.getAmount(),
                request.getAction(),
                request.getSign_time()
        );

        if (!expectedSign.equals(request.getSign_string())) {
            log.error("Signature failed in Complete method {}", request.getSign_string());
            return buildError(ClickErrorCode.SIGN_CHECK_FAILED, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        if (request.getAction() != 1) {
            log.error("Action failed in Complete method {}", request.getAction());
            return buildError(ClickErrorCode.ACTION_NOT_FOUND, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        Optional<ClickTransactionEntity> optionalTransaction = clickTransactionRepository.findByIdAndMerchantTransId(
                request.getMerchant_prepare_id().longValue(), request.getMerchant_trans_id());

        if (optionalTransaction.isEmpty()) {
            log.error("Transaction not exists error in Complete method {}", request);
            return buildError(ClickErrorCode.TRANSACTION_NOT_EXIST, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        ClickTransactionEntity transaction = optionalTransaction.get();

        if (transaction.getStatus() == ClickTransactionStatus.PAID) {
            log.error("Transaction already paid error in Complete method {}", request);
            return buildError(ClickErrorCode.ALREADY_PAID, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        if (transaction.getStatus() == ClickTransactionStatus.CANCELLED) {
            log.error("Transaction canceled error in Complete method {}", request);
            return buildError(ClickErrorCode.TRANSACTION_CANCELLED, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        if (!transaction.getAmount().equals(Double.parseDouble(request.getAmount()))) {
            transaction.setStatus(ClickTransactionStatus.CANCELLED);
            ClickTransactionEntity cancelledTransaction = clickTransactionRepository.save(transaction);
            log.info("Transaction cancelled: {}", cancelledTransaction);
            return buildError(ClickErrorCode.INCORRECT_AMOUNT, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        try {

            log.info("transaction begun at status");
            transaction.setStatus(ClickTransactionStatus.PAID);
            log.info("transaction begun at save");
            transaction = clickTransactionRepository.save(transaction);

            log.info("Transaction paid: {}", /*objectMapper.writeValueAsString(paidTransaction)*/ transaction.toString());
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
            return buildError(ClickErrorCode.FAILED_TO_UPDATE_USER, request.getMerchant_trans_id(), request.getMerchant_prepare_id(), request.getClick_trans_id());
        }

        ClickResponse clickResponse = buildSuccess(request.getClick_trans_id(), request.getMerchant_trans_id(), transaction.getId().intValue());
        log.info("SUCCESS RESPONSE in Complete-method: {}", clickResponse);
        return clickResponse;
    }

    private ClickResponse buildError(ClickErrorCode code,
                                     String merchantTransId,
                                     Integer merchantPrepareId,
                                     Long clickTransId) {
        return ClickResponse.builder()
                .click_trans_id(clickTransId)
                .merchant_trans_id(merchantTransId)
                .merchant_prepare_id(merchantPrepareId)
                .error(code.getCode())
                .error_note(code.getNote())
                .build();
    }

    private ClickResponse buildSuccess(Long clickTransId, String merchantTransId, Integer merchantPrepareId) {
        return ClickResponse.builder()
                .click_trans_id(clickTransId)
                .merchant_trans_id(merchantTransId)
                .merchant_prepare_id(merchantPrepareId)
                .error(ClickErrorCode.SUCCESS.getCode())
                .error_note(ClickErrorCode.SUCCESS.getNote())
                .build();
    }

}
