package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.AuthService;
import quvoncuz.service.ProfileService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegistrationRequestDTO dto) {
        if (profileService.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email already exists!");
        }
        if (profileService.existsByUsername(dto.getUsername())) {
            throw new AlreadyExistsException("Username already exists!");
        }

        ProfileEntity profile = profileService.create(dto);

        String accessToken = jwtUtil.encodeAccessToken(
                profile.getUsername(),
                profile.getRole()
        );

        logger.info("New user registered: {}", profile.getUsername());

        return AuthResponse.builder()
                .username(profile.getUsername())
                .role(profile.getRole())
                .token(accessToken)
                .build();
    }

    @Override
    @Transactional()
    public AuthResponse login(LoginRequestDTO dto) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getUsername(),
                                dto.getPassword()
                        )
                );

        ProfileEntity profile = profileRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        String accessToken = jwtUtil.encodeAccessToken(
                profile.getUsername(),
                profile.getRole()
        );

        return AuthResponse
                .builder()
                .username(profile.getUsername())
                .role(profile.getRole())
                .token(accessToken)
                .build();
    }
}
