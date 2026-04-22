package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;

import java.util.Optional;

public interface ProfileService {
    ProfileEntity create(RegistrationRequestDTO dto);

    ProfileFullInfo updateProfile(UpdateProfileRequestDTO dto, Long profileId, Long loginId);

    Optional<ProfileEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Boolean deleteById(Long id, Long adminId);

    ProfileDTO getProfileById(Long id, Long adminId);

    Page<ProfileDTO> getAllProfiles(Long adminId, int page, int size);

    void updateRole(Role role, long userId);

    ProfileEntity findById(Long profileId);
}
