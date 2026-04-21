package quvoncuz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import quvoncuz.enums.Gender;

@Data
public class RegistrationRequestDTO {
    @NotBlank(message = "Full name required")
    private String fullName;
    @NotBlank(message = "Username required")
    private String username;
    @NotBlank(message = "Email required")
    private String email;
    @NotBlank(message = "Password required")
    private String password;
    @NotNull(message = "Gender required")
    private Gender gender;
}
