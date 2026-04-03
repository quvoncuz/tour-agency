package quvoncuz.dto.tour;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTourRequestDTO {
    private String title;
    private String description;
    private String destination;
    private Long price;
    private Integer durationDays;
    private Integer maxSeats;
    private LocalDate startDate;
    private LocalDate endDate;
}
