package quvoncuz.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBookingRequestDTO {
    @NotNull(message = "Tour id is mandatory")
    @Positive(message = "Tour id is invalid")
    private Long tourId;
    @NotNull(message = "Seats booked is mandatory")
    @Positive(message = "Seats must be positive")
    private Integer seatsBooked;
    @Size(max = 255, message = "Note length restricted")
    private String note;
}
