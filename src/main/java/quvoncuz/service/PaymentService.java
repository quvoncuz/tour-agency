package quvoncuz.service;

import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;

import java.util.List;

public interface PaymentService {

    PaymentFullInfo processPayment(PaymentRequestDTO dto, Long userId);

    // ADMIN
    List<PaymentShortInfo> findAll(Long userId, int page, int size);

    List<PaymentShortInfo> findAllByUserId(Long userId, int page, int size);

    //ADMIN and AGENCY
    List<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size);
}
