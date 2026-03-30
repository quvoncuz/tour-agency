package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.ProfileService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Value("${admin.default.username}")
    private String adminUsername;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @PostConstruct
    public void initDefaultAdmin() {
        if (!existsByUsername(adminUsername)) {
            ProfileEntity admin = new ProfileEntity(
                    null,
                    "admin",
                    adminUsername,
                    adminEmail,
                    adminPassword,
                    BigDecimal.valueOf(0L),
                    Role.ADMIN,
                    Gender.MALE,
                    false,
                    true
            );
            profileRepository.createOrReplace(List.of(admin), true);
        } else {
            System.out.printf("Default admin user already exists: %s%n", adminUsername);
        }
    }

    @Override
    public ProfileEntity create(RegistrationRequestDTO dto) {

        ProfileEntity profile = new ProfileEntity(
                null,
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

        profileRepository.createOrReplace(List.of(profile), true);

        return profile;
    }

    @Override
    public ProfileEntity findByUsername(String username) {
        return profileRepository.findAll().stream().filter(profile -> profile.getUsername().equals(username))
                .findFirst().orElseThrow(() -> new NotFoundException("Username not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        Optional<ProfileEntity> profileByUsername = profileRepository.findAll()
                .stream()
                .filter(profile -> profile.getUsername().equals(username))
                .findFirst();

        return profileByUsername.isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<ProfileEntity> profileByEmail = profileRepository.findAll()
                .stream()
                .filter(profile -> profile.getEmail().equals(email))
                .findFirst();

        return profileByEmail.isPresent();
    }

    @Override
    public boolean deleteById(Long id, Long adminId) {
        List<ProfileEntity> allProfile = profileRepository.findAll();
        ProfileEntity admin = allProfile.stream().filter(p -> p.getId().equals(adminId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Admin with id " + adminId + " not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }
        ProfileEntity profile = allProfile.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Profile with id " + id + " not found"));
        allProfile.remove(profile);
        profileRepository.createOrReplace(allProfile, false);
        return true;
    }

    @Override
    public ProfileDTO getProfileById(Long id, Long adminId) {
        List<ProfileEntity> allProfile = profileRepository.findAll();
        ProfileEntity admin = allProfile.stream().filter(p -> p.getId().equals(adminId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Admin with id " + adminId + " not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }
        return allProfile
                .stream()
                .filter(profile -> profile.getId().equals(id))
                .map(ProfileMapper::toDTO)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Profile with id " + id + " not found"));
    }

    @Override
    public List<ProfileDTO> getAllProfiles(Long adminId) {
        List<ProfileEntity> allProfile = profileRepository.findAll();
        ProfileEntity admin = allProfile.stream().filter(p -> p.getId().equals(adminId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Admin with id " + adminId + " not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }
        return allProfile
                .stream()
                .map(ProfileMapper::toDTO)
                .toList();
    }

    @Override
    public void updateRole(Role role, long userId) {
        List<ProfileEntity> all = profileRepository.findAll();
        all.stream()
                .filter(p -> p.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Profile with id " + userId + " not found"))
                .setRole(role);
        profileRepository.createOrReplace(all, false);
    }
}
