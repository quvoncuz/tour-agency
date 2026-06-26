package quvoncuz.integration.agency;

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
import quvoncuz.dto.agency.UpdateAgencyRequestDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgencyAgencyControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private ProfileRepository profileRepository;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static final Long USER_ID = 1L;
    private static final Long AGENCY_ID = 1L;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);

        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("agency@mail.com");
        profile.setRole(Role.AGENCY);
        profile.setIsActive(true);
        profile.setVisible(true);
        profileRepository.save(profile);

        AgencyEntity agency = new AgencyEntity();
        agency.setId(USER_ID);
        agency.setOwnerId(USER_ID);
        agency.setName("Test Agency");
        agency.setPhone("998909009090");
        agency.setEmail("email@gmail.com");
        agency.setCity("Tashkent");
        agency.setStatus(AgencyStatus.ACCEPTED);
        agency.setVisible(true);
        agencyRepository.save(agency);

    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void update_Success() throws Exception {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("qwerty");
        dto.setPhone("999999999999");
        dto.setEmail("qwerty@gmail.com");
        dto.setDescription("qwerty");
        dto.setCity("qwerty");
        dto.setAddress("qwerty");

        mockMvc.perform(put("/agency/agencies/{agencyId}", AGENCY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("qwerty"));

        AgencyEntity agency = agencyRepository.findById(AGENCY_ID).orElseThrow();
        assertEquals("qwerty", agency.getDescription());
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void update_DoNotMatch_Throws() throws Exception {
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("agenc@mail.com");
        profile.setRole(Role.AGENCY);
        profile.setIsActive(true);
        profile.setVisible(true);
        profileRepository.save(profile);

        AgencyEntity agency = new AgencyEntity();
        agency.setId(2L);
        agency.setOwnerId(2L);
        agency.setName("Tes Agency");
        agency.setPhone("99909009090");
        agency.setEmail("emal@gmail.com");
        agency.setCity("Tashkent");
        agency.setStatus(AgencyStatus.ACCEPTED);
        agency.setVisible(true);
        agencyRepository.save(agency);

        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("qwerty");
        dto.setPhone("999999999999");
        dto.setEmail("qwerty@gmail.com");
        dto.setDescription("qwerty");
        dto.setCity("qwerty");
        dto.setAddress("qwerty");

        mockMvc.perform(put("/agency/agencies/{agencyId}", 2)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}