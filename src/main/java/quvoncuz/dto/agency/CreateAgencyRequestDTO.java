package quvoncuz.dto.agency;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAgencyRequestDTO {
    @Size(max = 100, message = "Name size restricted")
    @NotBlank(message = "Name required")
    private String name;
    @Size(min = 9, max = 13, message = "Invalid phone number")
    @NotBlank(message = "Phone required")
    private String phone;
    @Size(max = 255, message = "Email size restricted")
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