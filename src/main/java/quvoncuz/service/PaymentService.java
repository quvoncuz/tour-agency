package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;

public interface PaymentService {

    PaymentFullInfo processPayment(PaymentRequestDTO dto, Long userId);

    // ADMIN
    Page<PaymentShortInfo> findAll(Long userId, int page, int size);

    Page<PaymentShortInfo> findAllByUserId(Long userId, int page, int size);

    //ADMIN and AGENCY
    Page<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size);
}
