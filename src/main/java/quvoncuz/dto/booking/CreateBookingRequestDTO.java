package quvoncuz.dto.booking;

import lombok.Data;

@Data
public class CreateBookingRequestDTO {
    private Long tourId;
    private Integer seatsBooked;
    private String note;
}
