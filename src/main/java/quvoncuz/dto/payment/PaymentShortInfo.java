package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

@Data
@Builder
@AllArgsConstructor
public class PaymentShortInfo {
    private Long id;
    private Long tourId;
    private Long bookingId;
    private PaymentStatus status;
}
