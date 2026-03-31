package quvoncuz.dto.booking;

import lombok.Data;
import quvoncuz.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long tourId;
    private Integer seatsBooked;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String note;
    private LocalDateTime bookedAt;
}
