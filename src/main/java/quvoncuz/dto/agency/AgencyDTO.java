package quvoncuz.dto.agency;

import lombok.AllArgsConstructor;
import lombok.Data;
import quvoncuz.enums.AgencyStatus;

@Data
@AllArgsConstructor
public class AgencyDTO {
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
}
