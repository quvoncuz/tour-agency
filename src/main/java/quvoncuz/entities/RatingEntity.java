package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import quvoncuz.enums.RatingType;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    @Column(name = "source_id")
    private Long sourceId;

    @Column
    @Enumerated(EnumType.STRING)
    private RatingType type;

    @Column
    private Integer stars;

    @Column
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
