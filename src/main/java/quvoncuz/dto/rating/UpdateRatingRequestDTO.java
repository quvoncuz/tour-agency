package quvoncuz.dto.rating;

import lombok.Data;

@Data
public class UpdateRatingRequestDTO {
    private Integer stars;
    private String comment;
}
