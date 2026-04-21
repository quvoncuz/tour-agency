package quvoncuz.dto.agency;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAgencyRequestDTO {
    @NotBlank(message = "Name required")
    private String name;
    @NotBlank(message = "Phone required")
    private String phone;
    @Email(message = "Invalid Email")
    private String email;
    @Size(max = 255, message = "Description size restricted")
    @NotBlank(message = "Description required")
    private String description;
    @Size(max = 15, message = "Only city required")
    @NotBlank(message = "Location required")
    private String city;
    @Size(max = 255, message = "Address size restricted")
    @NotBlank(message = "Address required")
    private String address;
}