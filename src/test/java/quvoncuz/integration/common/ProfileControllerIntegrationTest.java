package quvoncuz.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.integration.BaseIntegrationTest;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProfileControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProfileRepository profileRepository;

    @MockitoBean
    private MockedStatic<SecurityUtil> mockedStatic;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = ProfileEntity.builder()
                .fullName("qwerty")
                .username("qwerty")
                .email("qwerty@gmail.com")
                .password(passwordEncoder.encode("qwerty"))
                .role(Role.USER)
                .isActive(true)
                .visible(true)
                .build();
        profile = profileRepository.save(profile);
        USER_ID = profile.getId();

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
        dto.setUsername("user");
        dto.setFullName("user");
        dto.setEmail("user@gmail.com");

        mockMvc.perform(put("/profiles/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("user"));

        ProfileEntity profile = profileRepository.findById(USER_ID).orElseThrow();
        Assertions.assertEquals("user@gmail.com", profile.getEmail());

    }
}