package quvoncuz.dto.booking;

import lombok.Data;

@Data
public class UpdateBookingRequestDTO {
    private Long bookingId;
    private Integer seats;
}
