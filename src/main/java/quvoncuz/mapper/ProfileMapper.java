package quvoncuz.mapper;

import quvoncuz.dto.ProfileDTO;
import quvoncuz.entities.ProfileEntity;

public class ProfileMapper {

    public static ProfileDTO toDTO(ProfileEntity profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .gender(profile.getGender())
                .token(null)
                .build();
    }
}