package quvoncuz.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTourRequestDTO {
    @Size(max = 255, message = "Title length restricted")
    @NotBlank(message = "Title is mandatory")
    private String title;
    @Size(max = 255, message = "Description length restricted")
    @NotBlank(message = "Description is mandatory")
    private String description;
    @Size(max = 255, message = "Destination length restricted")
    @NotBlank(message = "Destination is mandatory")
    private String destination;
    @Positive(message = "Price must be positive")
    private long price;
    @Positive(message = "Days must be positive")
    private int durationDays;
    @Positive(message = "Seats must be positive")
    private int maxSeats;
    @NotNull(message = "Starting date is mandatory")
    private LocalDate startDate;
    @NotNull(message = "End date is mandatory")
    private LocalDate endDate;
}
