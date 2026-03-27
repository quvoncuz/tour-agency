package quvoncuz.service;

import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;

public interface ProfileService {
    public ProfileDTO create(RegistrationRequestDTO dto);
    public ProfileEntity findByUsername(String username);
    public boolean checkUsernameIfExists(String username);
    public boolean checkEmailIfExists(String email);
}
