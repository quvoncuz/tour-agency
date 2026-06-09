package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.service.PaymentService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // ADMIN
    @Override
    public Page<PaymentShortInfo> findAll(Boolean refund, Long userId, Long tourId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PaymentEntity> result;

        if (refund) {
            log.info("Admin requested refund payments");
            result = paymentRepository
                    .findAllByStatusOrderByCreatedAtDesc(
                            PaymentStatus.REFUND,
                            pageRequest
                    );
        } else if (userId != 0) {
            log.info("Admin requested payments by user id");
            result = paymentRepository
                    .findAllByUserId(userId, pageRequest);

        } else if (tourId != 0) {
            log.info("Admin requested payments by tour id");
            result = paymentRepository
                    .findAllByTourId(tourId, pageRequest);

        } else {
            log.info("Admin requested all payments");
            result = paymentRepository.findAll(pageRequest);
        }

        return result.map(PaymentMapper::toShortInfo);
    }

    // ADMIN and USER himself
    @Override
    public Page<PaymentShortInfo> findAllByUserId(Long userId, int page, int size) {

        log.info("User with ID: {} requested their payment history", userId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return paymentRepository.findAllByUserId(userId, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }
}
