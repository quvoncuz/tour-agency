package quvoncuz.dto.rating;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRatingRequestDTO {
    @Positive(message = "Id must be positive")
    private int stars;
    @Size(max = 255, message = "Comment length restricted")
    private String comment;
}
