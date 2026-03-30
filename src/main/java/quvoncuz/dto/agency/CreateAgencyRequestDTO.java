package quvoncuz.dto.agency;

import lombok.Data;

@Data
public class CreateAgencyRequestDTO {
    private String name;
    private String phone;
    private String email;
    private String description;
    private String city;
    private String address;
}