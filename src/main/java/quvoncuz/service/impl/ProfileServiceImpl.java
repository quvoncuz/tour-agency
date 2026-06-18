package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.ProfileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public ProfileEntity create(RegistrationRequestDTO dto) {

        ProfileEntity profile = ProfileMapper.toEntity(dto);
        profile.setPassword(passwordEncoder.encode(dto.getPassword()));

        profile = profileRepository.save(profile);

        log.info("Created new profile with username: {}", profile.getUsername());
        return profile;
    }

    @Override
    @Transactional
    public ProfileDTO updateProfile(UpdateProfileRequestDTO dto, Long userId) {

        try {
            ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));
            profile.setFullName(dto.getFullName());
            profile.setUsername(dto.getUsername());
            profile.setEmail(dto.getEmail());

            profile = profileRepository.save(profile);

            String token = jwtUtil.encodeAccessToken(profile.getUsername(), profile.getRole());

            ProfileDTO profileDTO = ProfileMapper.toDTO(profile);
            profileDTO.setToken(token);

            return profileDTO;
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Username or Email already exists");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        profileRepository.updateVisible(false, id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDTO getProfileById(Long userId) {
        log.info("Retrieved profile with id: {}", userId);
        return ProfileMapper.toDTO(findById(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileDTO> getAllProfiles(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        log.info("Retrieved all profiles for admin");
        return profileRepository.findAll(pageRequest)
                .map(ProfileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileEntity findById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}

