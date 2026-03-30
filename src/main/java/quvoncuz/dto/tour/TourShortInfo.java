package quvoncuz.dto.tour;

import lombok.Data;
import quvoncuz.enums.TourStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TourShortInfo {
    private Long id;
    private Long agencyId;
    private String title;
    private String destination;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxSeats;
    private LocalDate startDate;
    private Long viewCount;
    private Double rating;
    private TourStatus status;
}
