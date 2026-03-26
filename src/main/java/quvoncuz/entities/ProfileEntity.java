package quvoncuz.entities;

import lombok.Data;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;

@Data
public class ProfileEntity {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Gender gender;
    private boolean isActive;
}
