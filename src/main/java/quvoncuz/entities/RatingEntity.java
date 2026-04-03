package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import quvoncuz.enums.RatingType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class RatingEntity {
    private Long id;
    private Long userId;
    private Long sourceId;
    private RatingType type;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;

    public RatingEntity() {
        this.createdAt = LocalDateTime.now();
    }
}
