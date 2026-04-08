package quvoncuz.mapper;

import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.ProfileShortInfo;
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

    public static ProfileEntity toEntity(RegistrationRequestDTO dto) {
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

    public static ProfileShortInfo toShortInfo(ProfileEntity entity) {
        return ProfileShortInfo.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static ProfileFullInfo toFullInfo(ProfileEntity entity) {
        return ProfileFullInfo.builder()
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .balance(entity.getBalance())
                .gender(entity.getGender())
                .build();
    }
}