package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_tour")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedTourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    @Column(name = "tour_id")
    private Long tourId;
    @ManyToOne
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    private TourEntity tour;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
