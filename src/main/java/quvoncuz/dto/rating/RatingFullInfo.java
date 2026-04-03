package quvoncuz.dto.rating;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingFullInfo {
    private Long userId;
    private Long sourceId;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;
}
