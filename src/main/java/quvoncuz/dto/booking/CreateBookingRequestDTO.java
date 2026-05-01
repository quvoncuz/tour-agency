package quvoncuz.dto.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBookingRequestDTO {
    @Positive(message = "Tour id is invalid")
    private long tourId;
    @Positive(message = "Seats must be positive")
    private int seatsBooked;
    @Size(max = 255, message = "Note length restricted")
    private String note;
}
