package quvoncuz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @Size(max = 100, message = "Username length restricted")
    @NotBlank(message = "Username required")
    private String username;
    @Size(max = 255, message = "Password length restricted")
    @NotBlank(message = "Password required")
    private String password;

}
