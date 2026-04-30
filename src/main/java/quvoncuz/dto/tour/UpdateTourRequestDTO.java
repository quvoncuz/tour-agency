package quvoncuz.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTourRequestDTO {
    @Size(max = 255, message = "Title length restricted")
    @NotBlank(message = "Title is mandatory")
    private String title;
    @Size(max = 255, message = "Description length restricted")
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "Destination is mandatory")
    private String destination;
    @Positive(message = "Days must be positive")
    private int durationDays;
    @Positive(message = "Seats must be positive")
    private int maxSeats;
    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;
    @NotNull(message = "End date is mandatory")
    private LocalDate endDate;
}
