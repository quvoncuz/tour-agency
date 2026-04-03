package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {

    private Long id;
    private Long userId;
    private Long tourId;
    private Integer seatsBooked;
    private Long paidAmount;
    private Long totalPrice;
    private BookingStatus status;
    private String note;
    private LocalDateTime bookedAt;
}
