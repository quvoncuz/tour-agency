package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.ProfileService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    private long profileId = 1;

    @Override
    public ProfileDTO create(RegistrationRequestDTO dto) {
        ProfileEntity profile = new ProfileEntity(
                profileId++,
                dto.getFullName(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                BigDecimal.valueOf(0),
                Role.USER,
                dto.getGender(),
                false,
                true
        );

        profileRepository.create(List.of(profile), true);

        return ProfileMapper.toDTO(profile);
    }

    @Override
    public ProfileEntity findByUsername(String username){
        return profileRepository.getAllProfile().stream().filter(profile -> profile.getUsername().equals(username))
                .findFirst().orElseThrow(() -> new NotFoundException("Username not found"));
    }

    @Override
    public boolean checkUsernameIfExists(String username) {
        Optional<ProfileEntity> profileByUsername = profileRepository.getAllProfile()
                .stream()
                .filter(profile -> profile.getUsername().equals(username))
                .findFirst();

        return profileByUsername.isPresent();
    }

    @Override
    public boolean checkEmailIfExists(String email) {
        Optional<ProfileEntity> profileByEmail = profileRepository.getAllProfile()
                .stream()
                .filter(profile -> profile.getEmail().equals(email))
                .findFirst();

        return profileByEmail.isPresent();
    }
}
