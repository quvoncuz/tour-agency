package quvoncuz.dto.tour;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SaveTourRequestDTO {
    @Positive(message = "Id must be positive")
    private long tourId;
}
