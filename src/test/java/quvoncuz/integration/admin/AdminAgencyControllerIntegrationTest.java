package quvoncuz.integration.admin;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.dto.agency.AgencyApproveRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.Role;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAgencyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AgencyRepository agencyRepository;

    private MockedStatic<SecurityUtil> mockedStatic;
    private ProfileEntity savedProfile;
    private static Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = ProfileEntity.builder()
                .role(Role.AGENCY)
                .visible(true)
                .build();
        savedProfile = profileRepository.save(profile);
        USER_ID = savedProfile.getId();

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void approveAgency_WithAcceptance_Success() throws Exception {
        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(savedProfile.getId());
        dto.setApprove(true);

        AgencyEntity agency = AgencyEntity.builder()
                .id(savedProfile.getId())
                .ownerId(savedProfile.getId())
                .name("Qwerty")
                .phone("9999999999")
                .status(AgencyStatus.PENDING)
                .email("mail")
                .city("qwerty")
                .build();
        agencyRepository.save(agency);

        mockMvc.perform(post("/admin/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());

        agency = agencyRepository.findById(savedProfile.getId()).orElseThrow();
        Assertions.assertEquals(AgencyStatus.ACCEPTED, agency.getStatus());
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void approveAgency_WithRejection_Success() throws Exception {
        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(savedProfile.getId());
        dto.setApprove(false);

        AgencyEntity agency = AgencyEntity.builder()
                .id(savedProfile.getId())
                .ownerId(savedProfile.getId())
                .name("Qwerty")
                .phone("9999999999")
                .status(AgencyStatus.PENDING)
                .email("mail")
                .city("qwerty")
                .build();
        agencyRepository.save(agency);

        mockMvc.perform(post("/admin/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());

        agency = agencyRepository.findById(savedProfile.getId()).orElseThrow();
        Assertions.assertEquals(AgencyStatus.REJECTED, agency.getStatus());
    }
}