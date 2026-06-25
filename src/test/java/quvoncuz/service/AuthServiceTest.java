package quvoncuz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock ProfileService profileService;
    @Mock ProfileRepository profileRepository;
    @Mock JwtUtil jwtUtil;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthServiceImpl authService;

    private ProfileEntity profile;
    private RegistrationRequestDTO registerDto;
    private LoginRequestDTO loginDto;

    @BeforeEach
    void setUp() {
        profile = new ProfileEntity();
        profile.setId(1L);
        profile.setUsername("quvonch");
        profile.setRole(Role.USER);

        registerDto = new RegistrationRequestDTO();
        registerDto.setUsername("quvonch");
        registerDto.setEmail("quvonch@mail.ru");
        registerDto.setPassword("secret123");

        loginDto = new LoginRequestDTO();
        loginDto.setUsername("quvonch");
        loginDto.setPassword("secret123");
    }

}
