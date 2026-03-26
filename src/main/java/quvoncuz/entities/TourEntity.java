package quvoncuz.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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
    private LocalDateTime createdDate;
}
