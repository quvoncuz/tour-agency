package quvoncuz.service;

import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;

import java.util.List;

public interface ProfileService {
    public ProfileEntity create(RegistrationRequestDTO dto);

    public ProfileEntity findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    public Boolean deleteById(Long id, Long adminId);

    ProfileDTO getProfileById(Long id, Long adminId);

    List<ProfileDTO> getAllProfiles(Long adminId, int page, int size);

    public void updateRole(Role role, long userId);
}
