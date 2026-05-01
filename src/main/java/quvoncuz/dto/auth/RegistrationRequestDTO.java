package quvoncuz.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import quvoncuz.enums.Gender;

@Data
public class RegistrationRequestDTO {
    @Size(min = 2, max = 50, message = "Full name length restricted")
    @NotBlank(message = "Full name required")
    private String fullName;
    @Size(max = 100, message = "Username length restricted")
    @NotBlank(message = "Username required")
    private String username;
    @Size(max = 255, message = "Email length restricted")
    @Email(message = "Email required")
    private String email;
    @Size(max = 255, message = "Password length restricted")
    @NotBlank(message = "Password required")
    private String password;
    @NotNull(message = "Gender required")
    private Gender gender;
}
