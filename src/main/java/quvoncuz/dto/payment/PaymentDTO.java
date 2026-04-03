package quvoncuz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import quvoncuz.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long userId;
    private Long bookingId;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime createdDate;
}
