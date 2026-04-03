package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class PaymentFullInfo {
    private Long tourId;
    private Long bookingId;
    private Long amount;
    private PaymentStatus status;
    private LocalDate createdDate;
}
