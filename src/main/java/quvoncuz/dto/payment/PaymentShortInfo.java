package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentShortInfo {
    private Long id;
    private Long tourId;
    private Long bookingId;
    private PaymentStatus status;
}
