package quvoncuz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private BigDecimal balance = BigDecimal.valueOf(0);
    private Role role;
    private Gender gender;
    private Boolean isCreateAgency = false;
    private Boolean isActive = true;
}
