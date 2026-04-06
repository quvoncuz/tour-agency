package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

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

    @Column(name = "booking_id")
    private Long bookingId;
    @ManyToOne
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private BookingEntity booking;

    @Column
    private Long amount;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}