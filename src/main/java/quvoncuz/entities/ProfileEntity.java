package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Long balance = 0L;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "is_created_agency")
    private Boolean isCreateAgency = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "owner")
    private AgencyEntity agency;

    @OneToMany(mappedBy = "user")
    private List<BookingEntity> bookings;

    @OneToMany(mappedBy = "user")
    private List<PaymentEntity> payments;

    @OneToMany(mappedBy = "user")
    private List<SavedTourEntity> savedTours;
}
