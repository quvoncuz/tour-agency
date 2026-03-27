package quvoncuz.mapper;

import org.springframework.stereotype.Component;
import quvoncuz.dto.ProfileDTO;
import quvoncuz.entities.ProfileEntity;

@Component
public class ProfileMapper {

    public static ProfileDTO toDTO(ProfileEntity profile){
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setFullName(profile.getFullName());
        dto.setUsername(profile.getUsername());
        dto.setEmail(profile.getEmail());
        dto.setGender(profile.getGender());
        return dto;
    }

}
