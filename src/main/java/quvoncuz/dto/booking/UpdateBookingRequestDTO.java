package quvoncuz.dto.booking;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateBookingRequestDTO {
    @Positive(message = "Booking id is invalid")
    private long bookingId;
    @Positive(message = "Seats must be positive")
    private int seats;
}
