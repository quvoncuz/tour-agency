package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
