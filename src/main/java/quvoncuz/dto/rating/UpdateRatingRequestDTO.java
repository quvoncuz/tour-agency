package quvoncuz.dto.rating;

import lombok.Data;
import quvoncuz.enums.RatingType;

@Data
public class UpdateRatingRequestDTO {
    private Long id;
    private Long sourceId;
    private RatingType type;
    private Integer stars;
    private String comment;
}
