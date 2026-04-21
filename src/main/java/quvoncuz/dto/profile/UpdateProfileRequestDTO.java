package quvoncuz.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestDTO {

    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Email(message = "Invalid email")
    private String email;
}
