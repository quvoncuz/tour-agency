package quvoncuz.dto.agency;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgencyFullInfo {
    private Long id;
    private Long ownerId;
    private String name;
    private String phone;
    private String email;
    private String description;
    private String city;
    private String address;
    private Double rating;
}
