package quvoncuz.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import quvoncuz.enums.RatingType;

@Data
public class RatingRequestDTO {
    @Positive(message = "Id must be positive")
    private long sourceId;
    private RatingType type;
    @Min(value = 1, message = "Stars must be between 1 and 5")
    @Max(value = 5, message = "Stars must be between 1 and 5")
    private int stars;
    @Size(max = 255, message = "Comment length restricted")
    private String comment;
}
