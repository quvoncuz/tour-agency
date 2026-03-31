package quvoncuz.dto.booking;

import lombok.Data;
import quvoncuz.enums.BookingStatus;

import java.math.BigDecimal;

@Data
public class CreateBookingRequestDTO {
    private Long tourId;
    private Integer seatsBooked;
    private String note;
}
