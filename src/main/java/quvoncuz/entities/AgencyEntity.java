package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.AgencyStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyEntity {

    private Long id;
    private Long ownerId;
    private String name;
    private String phone;
    private String email;
    private String description;
    private String city;
    private String address;
    private Boolean approved;
    private Double rating;
    private AgencyStatus status;
}
