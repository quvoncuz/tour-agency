package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.payment.PaymentShortInfo;

public interface PaymentService {

    Page<PaymentShortInfo> findAllByRefund(int page, int size);

    // ADMIN
    Page<PaymentShortInfo> findAll(int page, int size);

    Page<PaymentShortInfo> findAllByUserId(int page, int size);

    //ADMIN and AGENCY
    Page<PaymentShortInfo> findAllByTourId(Long tourId, int page, int size);
}
