    package quvoncuz.dto.tour;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TourFullInfo {
    private Long id;
    private Long agencyId;
    private String title;
    private String description;
    private String destination;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxSeats;
    private Integer availableSeats;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long viewCount;
    private Double rating;
}
