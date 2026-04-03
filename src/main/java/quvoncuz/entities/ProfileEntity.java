package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private Long balance = 0L;
    private Role role;
    private Gender gender;
    private Boolean isCreateAgency = false;
    private Boolean isActive = true;
}
