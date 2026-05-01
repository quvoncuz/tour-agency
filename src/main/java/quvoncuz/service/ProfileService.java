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

    ProfileFullInfo updateProfile(UpdateProfileRequestDTO dto, Long profileId);

    Optional<ProfileEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Boolean deleteById(Long id);

    ProfileDTO getProfileById(Long id);

    Page<ProfileDTO> getAllProfiles(int page, int size);

    void updateRole(Role role, long userId);

    ProfileEntity findById(Long profileId);
}
