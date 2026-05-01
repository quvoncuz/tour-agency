package quvoncuz.security.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import quvoncuz.enums.Role;

@Getter
@Setter
@Builder
public class JwtDTO {
    private String username;
    private Role role;
}
