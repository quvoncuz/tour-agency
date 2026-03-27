package quvoncuz.dto;

import lombok.Data;
import quvoncuz.enums.Gender;

@Data
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Gender gender;
    private String token;
}
