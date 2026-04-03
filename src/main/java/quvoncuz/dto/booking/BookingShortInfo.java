package quvoncuz.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortInfo {
    private Long id;
    private Long tourId;
    private Integer seatsBooked;
    private Long totalPrice;
    private BookingStatus status;
}
