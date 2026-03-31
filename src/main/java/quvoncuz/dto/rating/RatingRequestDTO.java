package quvoncuz.dto.rating;

import lombok.Data;
import quvoncuz.enums.RatingType;

@Data
public class RatingRequestDTO {
    private Long sourceId;
    private RatingType type;
    private Integer stars;
    private String comment;
}
