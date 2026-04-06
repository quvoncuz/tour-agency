package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import quvoncuz.enums.TourStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tour")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agency_id")
    private Long agencyId;
    @ManyToOne
    @JoinColumn(name = "agency_id", insertable = false, updatable = false)
    private AgencyEntity agency;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String destination;

    @Column
    private Long price;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "max_seats")
    private Integer maxSeats;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "view_count")
    private Long viewCount;

    @Column
    private Double rating;

    @Column
    @Enumerated(EnumType.STRING)
    private TourStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tour")
    private List<BookingEntity> bookings;

    @OneToMany(mappedBy = "tour")
    private List<PaymentEntity> payments;
}
