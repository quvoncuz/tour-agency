package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.TourStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {
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
    private Boolean isActive;
    private Long viewCount;
    private Double rating;
    private TourStatus status;
    private LocalDateTime createdDate;
}
