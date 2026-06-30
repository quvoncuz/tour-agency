package quvoncuz.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.agency.CreateAgencyRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.Role;
import quvoncuz.integration.BaseIntegrationTest;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.util.SecurityUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgencyControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private ProfileRepository profileRepository;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static Long USER_ID = 1L;
    private static Long AGENCY_ID = 1L;

    @BeforeEach
    void setUp() {

        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("user@mail.com");
        profile.setRole(Role.USER);
        profile.setIsActive(true);
        profile.setVisible(true);
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
    void applyForAgency_Success() throws Exception {
        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("Test Agency");
        dto.setPhone("998909009090");
        dto.setEmail("email@gmail.com");
        dto.setCity("Tashkent");
        dto.setDescription("qwerty");
        dto.setAddress("qwerty");

        mockMvc.perform(post("/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.city").value("Tashkent"));

        AgencyEntity agency = agencyRepository.findAll().get(0);

        assertEquals(AgencyStatus.PENDING, agency.getStatus());
        assertEquals("qwerty", agency.getDescription());
        assertEquals("email@gmail.com", agency.getEmail());
    }

    @Test
    @WithMockUser
    void applyForAgency_AlreadyExists_Throws() throws Exception {
        ProfileEntity profile = profileRepository.findById(USER_ID).orElseThrow();
        profile.setIsCreatedAgency(true);

        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("Test Agency");
        dto.setPhone("998909009090");
        dto.setEmail("email@gmail.com");
        dto.setCity("Tashkent");
        dto.setDescription("qwerty");
        dto.setAddress("qwerty");

        mockMvc.perform(post("/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
}