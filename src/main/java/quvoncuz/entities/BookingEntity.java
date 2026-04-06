package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {

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

    @Column(name = "seats_booked")
    private Integer seatsBooked;

    @Column(name = "paid_amount")
    private Long paidAmount;

    @Column(name = "total_price")
    private Long totalPrice;

    @Column
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column
    private String note;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;
}