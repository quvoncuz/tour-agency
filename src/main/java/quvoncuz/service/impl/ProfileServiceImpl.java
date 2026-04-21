package quvoncuz.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.ProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;

    //    @Value("${admin.default.username}")
    private final String adminUsername = "admin";

    //    @Value("${admin.default.email}")
    private final String adminEmail = "admin@admin.uz";

    //    @Value("${admin.default.password}")
    private final String adminPassword = "Admin123";

//    @PostConstruct
    @Transactional(readOnly = true)
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
    @Transactional
    public ProfileEntity create(RegistrationRequestDTO dto) {

        ProfileEntity profile = ProfileMapper.toEntity(dto);

        profile = profileRepository.save(profile);

        logger.info("Created new profile with username: {}", profile.getUsername());
        return profile;
    }

    @Override
    @Transactional
    public ProfileFullInfo updateProfile(UpdateProfileRequestDTO dto, Long profileId, Long loginId) {

        if (profileId.equals(loginId)) {
            throw new DoNotMatchException("You can update yourself only");
        }

        ProfileEntity profile = profileRepository.findById(loginId).orElseThrow(() -> new NotFoundException("Profile not found"));
        profile.setFullName(dto.getFullName());
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());

        profile = profileRepository.save(profile);

        return ProfileMapper.toFullInfo(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileEntity> findByUsername(String username) {
        return profileRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return profileRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Boolean deleteById(Long id, Long adminId) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new PermissionDeniedException("You don't have permission");
        }
        profileRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDTO getProfileById(Long id, Long adminId) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new PermissionDeniedException("You don't have permission");
        }
        logger.info("Retrieved profile with id: {} for admin with id: {}", id, adminId);
        return ProfileMapper.toDTO(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileDTO> getAllProfiles(Long adminId, int page, int size) {
        ProfileEntity admin = findById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new PermissionDeniedException("You don't have permission");
        }

        List<ProfileEntity> allProfile = profileRepository.findAll(page - 1, size);
        logger.info("Retrieved all profiles for admin with id: {}", adminId);
        return allProfile
                .stream()
                .map(ProfileMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void updateRole(Role role, long userId) {
        profileRepository.updateProfileRole(role, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileEntity findById(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}

