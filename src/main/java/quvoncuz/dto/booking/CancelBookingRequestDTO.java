package quvoncuz.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBookingRequestDTO {
    private Long bookingId;
    private String cancelReason;
}
