package quvoncuz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.impl.ProfileServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileEntity profile;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        profile = new ProfileEntity();
        profile.setFullName("Ali Vali");
        profile.setUsername("AliVali");
        profile.setEmail("alivali@mail.ru");

    }

    @Test
    void updateProfile_Success() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO();
        dto.setUsername("ALI_VALI");
        dto.setFullName("Ali VALI");
        dto.setEmail("alivali@gmail.com");

        profile.setRole(Role.USER);
        String token = "jwt token";

        when(profileRepository.findById(USER_ID)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(jwtUtil.encodeAccessToken(eq("ALI_VALI"), eq(Role.USER))).thenReturn(token);

        ProfileDTO profileDTO = profileService.updateProfile(dto, USER_ID);

        assertNotNull(profileDTO);
        assertEquals("ALI_VALI", profileDTO.getUsername());
        assertEquals("alivali@gmail.com", profileDTO.getEmail());
        assertEquals(token, profileDTO.getToken());

        verify(profileRepository, times(1)).findById(USER_ID);
        verify(profileRepository, times(1)).save(any());
        verify(jwtUtil, times(1)).encodeAccessToken(dto.getUsername(), profile.getRole());
    }
}