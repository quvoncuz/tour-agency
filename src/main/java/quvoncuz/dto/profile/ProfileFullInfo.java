package quvoncuz.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import quvoncuz.enums.Gender;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFullInfo {

    private String fullName;

    private String username;

    private String email;

    private Gender gender;
}
