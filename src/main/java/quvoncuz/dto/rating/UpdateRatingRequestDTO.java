package quvoncuz.dto.rating;

import lombok.Data;

@Data
public class UpdateRatingRequestDTO {
    private int stars;
    private String comment;
}
