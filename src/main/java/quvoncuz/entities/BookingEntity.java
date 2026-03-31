package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {

    private Long id;
    private Long userId;
    private Long tourId;
    private Integer seatsBooked;
    private BigDecimal paidAmount;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String note;
    private LocalDateTime bookedAt;
}
