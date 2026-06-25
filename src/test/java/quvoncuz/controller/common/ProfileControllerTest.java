package quvoncuz.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.ProfileService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private ProfileService profileService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser
    void update_Success() throws Exception {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO();
        dto.setEmail("email@gmail.com");
        dto.setFullName("email");
        dto.setUsername("email");

        ProfileDTO profileDTO = ProfileDTO
                .builder()
                .id(USER_ID)
                .fullName("email")
                .username("email")
                .email("email@gmail.com")
                .build();

        when(profileService.updateProfile(dto, USER_ID)).thenReturn(profileDTO);

        mockMvc.perform(put("/profiles/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("email@gmail.com"));

        verify(profileService, times(1)).updateProfile(any(), eq(USER_ID));
    }

    @Test
    @WithMockUser
    void update_ThrowsException() throws Exception {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO();
        dto.setEmail("email@gmail.com");
        dto.setFullName("email");

        mockMvc.perform(put("/profiles/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profileService);
    }
}