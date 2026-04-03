package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SavedTourEntity {
    private Long id;
    private Long userId;
    private Long tourId;
    private LocalDateTime createdAt;

    public SavedTourEntity() {
        this.createdAt = LocalDateTime.now();
    }
}
