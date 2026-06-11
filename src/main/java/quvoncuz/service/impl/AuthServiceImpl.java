package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.EventType;
import quvoncuz.events.StatisticsEvent;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.AuthService;
import quvoncuz.service.ProfileService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public AuthResponse register(RegistrationRequestDTO dto) {

        try {
            ProfileEntity profile;
            profile = profileService.create(dto);
            String accessToken = jwtUtil.encodeAccessToken(
                    profile.getUsername(),
                    profile.getRole()
            );

            log.info("New user registered: {}", profile.getUsername());

            applicationEventPublisher.publishEvent(
                    StatisticsEvent.builder()
                            .entityId(profile.getId())
                            .eventType(EventType.USER_REGISTERED)
                            .dateTime(LocalDateTime.now())
                            .build());

            return AuthResponse.builder()
                    .username(profile.getUsername())
                    .role(profile.getRole())
                    .token(accessToken)
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Email or Username already exists");
        }
    }

    @Override
    public AuthResponse login(LoginRequestDTO dto) {

        ProfileEntity profile = profileRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getUsername(),
                                dto.getPassword()
                        )
                );


        String accessToken = jwtUtil.encodeAccessToken(
                profile.getUsername(),
                profile.getRole()
        );

        log.info("User logged in: {}", profile.getUsername());

        return AuthResponse
                .builder()
                .username(profile.getUsername())
                .role(profile.getRole())
                .token(accessToken)
                .build();
    }
}
