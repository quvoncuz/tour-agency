package quvoncuz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Username required")
    private String username;
    @NotBlank(message = "Password required")
    private String password;

}
