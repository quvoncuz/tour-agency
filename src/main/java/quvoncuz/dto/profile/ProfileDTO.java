package quvoncuz.dto.profile;

import lombok.Builder;
import lombok.Data;
import quvoncuz.enums.Gender;

@Data
@Builder
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Gender gender;
    private String token;
}
