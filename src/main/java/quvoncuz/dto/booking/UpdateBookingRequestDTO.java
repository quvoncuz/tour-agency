package quvoncuz.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateBookingRequestDTO {
    @NotNull(message = "Booking id is mandatory")
    @Positive(message = "Booking id is invalid")
    private Long bookingId;
    @NotNull(message = "Seats is mandatory")
    @Positive(message = "Seats must be positive")
    private Integer seats;
}
