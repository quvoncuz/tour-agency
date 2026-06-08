package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.payment.PaymentShortInfo;

public interface PaymentService {
    // ADMIN
    Page<PaymentShortInfo> findAll(Boolean refund, Long userId, Long tourId, int page, int size);

    Page<PaymentShortInfo> findAllByUserId(Long userId, int page, int size);
}
