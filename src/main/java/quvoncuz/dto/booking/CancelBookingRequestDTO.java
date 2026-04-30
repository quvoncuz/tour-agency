package quvoncuz.dto.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBookingRequestDTO {
    @Positive(message = "Id must be positive")
    private long bookingId;
    @Size(max = 255, message = "Cancel reason must be at most 255 characters")
    private String cancelReason;
}
