package quvoncuz.dto.tour;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateTourRequestDTO {
    private Long id;
    private String title;
    private String description;
    private String destination;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxSeats;
    private LocalDate startDate;
    private LocalDate endDate;
}
