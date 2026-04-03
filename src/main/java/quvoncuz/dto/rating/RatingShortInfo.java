package quvoncuz.dto.rating;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingShortInfo {
    private Long id;
    private Long userId;
    private Long sourceId;
    private Integer stars;
}
