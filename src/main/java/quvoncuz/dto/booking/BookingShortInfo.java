package quvoncuz.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortInfo {
    private Long id;
    private Long tourId;
    private Integer seatsBooked;
    private BigDecimal totalPrice;
    private BookingStatus status;
}
