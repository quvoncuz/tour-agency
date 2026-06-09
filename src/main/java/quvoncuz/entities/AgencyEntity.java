package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import quvoncuz.enums.AgencyStatus;

@Entity
@Table(name = "agencies", uniqueConstraints = @UniqueConstraint(columnNames = "owner_id"))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgencyEntity {

    @Id
    private Long id;

    @Column(name = "owner_id", unique = true)
    private Long ownerId;
    @OneToOne
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private ProfileEntity owner;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String description;

    @Column(nullable = false)
    private String city;

    @Column
    private String address;

    @Column
    private Boolean approved;

    @Column
    private Double rating;

    private Boolean visible = true;

    @Column
    @Enumerated(EnumType.STRING)
    private AgencyStatus status;
}