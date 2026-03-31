package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long userId;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdDate;
}
