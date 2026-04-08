package quvoncuz.dto.profile;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileShortInfo {

    private Long id;

    private String fullName;

    private String username;

    private String email;
}
