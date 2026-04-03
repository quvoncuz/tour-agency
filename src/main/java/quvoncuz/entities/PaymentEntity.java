package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    private Long id;
    private Long userId;
    private Long tourId;
    private Long bookingId;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;

}
