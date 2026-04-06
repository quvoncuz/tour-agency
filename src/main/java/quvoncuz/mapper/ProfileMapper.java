package quvoncuz.mapper;

import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;

import java.time.LocalDateTime;

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

    public static ProfileEntity toEntity(RegistrationRequestDTO dto){
        return ProfileEntity.builder()
                .id(null)
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .balance(0L)
                .role(Role.USER)
                .gender(dto.getGender())
                .isCreateAgency(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}