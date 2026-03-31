package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentFullInfo {
    private Long tourId;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDate createdDate;
}
