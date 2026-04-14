package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import quvoncuz.enums.AgencyStatus;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "agency")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgencyEntity {

    @Id
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;
    @OneToOne
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private ProfileEntity owner;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private String description;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private Boolean approved;

    @Column
    private Double rating;

    @Column
    @Enumerated(EnumType.STRING)
    private AgencyStatus status;

    @OneToMany(mappedBy = "agency")
    private List<TourEntity> tours;
}