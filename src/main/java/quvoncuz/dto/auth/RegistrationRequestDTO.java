package quvoncuz.dto.auth;

import lombok.Data;
import quvoncuz.enums.Gender;

@Data
public class RegistrationRequestDTO {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private Gender gender;
}
