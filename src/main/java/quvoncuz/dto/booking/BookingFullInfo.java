package quvoncuz.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFullInfo {
    private Long id;
    private Long tourId;
    private Integer seatsBooked;
    private Long paidAmount;
    private Long totalPrice;
    private BookingStatus status;
    private String note;
    private LocalDateTime bookedAt;
}
