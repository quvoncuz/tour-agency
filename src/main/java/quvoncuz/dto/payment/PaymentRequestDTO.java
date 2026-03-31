package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long tourId;
    private Long bookingId;
}
