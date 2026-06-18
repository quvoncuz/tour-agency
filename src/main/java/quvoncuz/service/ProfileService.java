package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;

public interface ProfileService {
    ProfileEntity create(RegistrationRequestDTO dto);

    ProfileDTO updateProfile(UpdateProfileRequestDTO dto, Long profileId);

    void deleteById(Long id);

    ProfileDTO getProfileById(Long id);

    Page<ProfileDTO> getAllProfiles(int page, int size);

    ProfileEntity findById(Long profileId);
}
