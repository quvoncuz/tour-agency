package quvoncuz.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.ProfileService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
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
            ProfileEntity admin = ProfileEntity.builder()
                    .fullName("admin")
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(adminPassword)
                    .role(Role.ADMIN)
                    .gender(Gender.MALE)
                    .isCreateAgency(false)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            logger.info("Creating default admin with username: {}", adminUsername);
            profileRepository.save(admin);
        } else {
            logger.info("Default admin already exists with username: {}", adminUsername);
        }
    }

    @Override
    public ProfileEntity create(RegistrationRequestDTO dto) {

        ProfileEntity profile = ProfileMapper.toEntity(dto);

        profileRepository.save(profile);

        logger.info("Created new profile with username: {}", profile.getUsername());
        return profile;
    }

    @Override
    public Optional<ProfileEntity> findByUsername(String username) {
        return profileRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return profileRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }

    @Override
    public Boolean deleteById(Long id, Long adminId) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }
        profileRepository.deleteById(id);
        return true;
    }

    @Override
    public ProfileDTO getProfileById(Long id, Long adminId) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }
        logger.info("Retrieved profile with id: {} for admin with id: {}", id, adminId);
        return ProfileMapper.toDTO(findById(id));
    }

    @Override
    public Page<ProfileDTO> getAllProfiles(Long adminId, int page, int size) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new NotFoundException("User with id " + adminId + " is not an admin");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ProfileEntity> allProfile = profileRepository.findAll(pageRequest);
        logger.info("Retrieved all profiles for admin with id: {}", adminId);
        return allProfile.map(ProfileMapper::toDTO);
    }

    @Override
    public void updateRole(Role role, long userId) {
        profileRepository.updateProfileRole(role, userId);
    }

    @Override
    public ProfileEntity findById(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new NotFoundException("User not found with id: " + profileId));
    }
}

