package quvoncuz.service;

import org.springframework.stereotype.Service;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;

import java.util.List;

@Service
public interface PaymentService {

    public void processPayment(PaymentRequestDTO dto, Long userId);

    public List<PaymentShortInfo> findAll(int page, int size);

    public List<PaymentShortInfo> findAllByUserId(Long userId, int page, int size);

    //ADMIN and AGENCY
    List<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size);
}
