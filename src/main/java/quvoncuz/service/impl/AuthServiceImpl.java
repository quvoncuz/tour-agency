package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.InvalidException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.service.AuthService;
import quvoncuz.service.ProfileService;

import java.util.Base64;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final ProfileService profileService;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    @Override
    @Transactional
    public AuthResponse register(RegistrationRequestDTO dto) {
        if (profileService.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email already exists!");
        }
        if (profileService.existsByUsername(dto.getUsername())) {
            throw new AlreadyExistsException("Username already exists!");
        }
//        if (!isValidEmail(dto.getEmail())){
//            throw new InvalidException("Invalid email");
//        }


        ProfileEntity profile = profileService.create(dto);

        String token = Base64.getEncoder().encodeToString((profile.getUsername() + ":" + profile.getPassword()).getBytes());
        logger.info("New user registered: {}, token: {}", profile.getUsername(), token);
        return new AuthResponse(
                profile.getUsername(),
                profile.getRole().name(),
                profile.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequestDTO dto) {
        ProfileEntity profile = profileService.findByUsername(dto.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        if (!profile.getPassword().equals(dto.getPassword())) {
            throw new DoNotMatchException("Username or password incorrect");
        }
        if (!profile.getIsActive()) {
            throw new InvalidException("Profile is not active");
        }

        logger.info("User logged in: {}", profile.getUsername());
        return new AuthResponse(
                profile.getUsername(),
                profile.getRole().name(),
                profile.getId());
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return pattern.matcher(email).matches();
    }
}
