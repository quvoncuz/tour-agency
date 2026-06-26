package quvoncuz.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.repository.ProfileRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_Success() throws Exception {
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setFullName("qwerty");
        dto.setEmail("email@gmail.com");
        dto.setGender(Gender.MALE);
        dto.setPassword("qwerty");
        dto.setUsername("qwerty");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("qwerty"))
                .andExpect(jsonPath("$.data.token").isString());

        ProfileEntity profile = profileRepository.findByUsername(dto.getUsername()).orElseThrow();
        assertEquals("email@gmail.com", profile.getEmail());
        assertEquals(Role.USER, profile.getRole());
    }

    @Test
    void register_AlreadyExists_Throws() throws Exception {
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("user@mail.com");
        profile.setUsername("user");
        profile.setPassword(passwordEncoder.encode("qwerty"));
        profile.setRole(Role.USER);
        profile.setIsActive(true);
        profile.setVisible(true);
        profileRepository.save(profile);

        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setFullName("qwerty");
        dto.setEmail("email@gmail.com");
        dto.setGender(Gender.MALE);
        dto.setPassword("qwerty");
        dto.setUsername("user");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void login_Success() throws Exception {
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("user@mail.com");
        profile.setUsername("user");
        profile.setPassword(passwordEncoder.encode("qwerty"));
        profile.setRole(Role.USER);
        profile.setIsActive(true);
        profile.setVisible(true);
        profileRepository.save(profile);

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("user");
        dto.setPassword("qwerty");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());

        profile = profileRepository.findByUsername(dto.getUsername()).orElseThrow();

        assertEquals(Role.USER, profile.getRole());
        assertEquals("user@mail.com", profile.getEmail());
    }

    @Test
    void login_NotFound_Throws() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("user");
        dto.setPassword("qwerty");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}