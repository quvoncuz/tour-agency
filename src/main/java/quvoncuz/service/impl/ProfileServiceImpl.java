package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.ProfileService;
import quvoncuz.util.SecurityUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;

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
    public ProfileFullInfo updateProfile(UpdateProfileRequestDTO dto, Long profileId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (profileId.equals(userId)) {
            throw new DoNotMatchException("You can update yourself only");
        }

        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));
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
    public Boolean deleteById(Long id) {
        profileRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDTO getProfileById(Long id) {
        Long loginId = SecurityUtil.getCurrentUserId();
        ProfileEntity login = findById(loginId);
        if (login.getRole() != Role.ADMIN && !login.getId().equals(id)) {
            throw new PermissionDeniedException("You don't have permission");
        }
        logger.info("Retrieved profile with id: {}", id);
        return ProfileMapper.toDTO(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileDTO> getAllProfiles(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        logger.info("Retrieved all profiles for admin");
        return profileRepository.findAll(pageRequest)
                .map(ProfileMapper::toDTO);
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

