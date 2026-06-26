package quvoncuz.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.integration.BaseIntegrationTest;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TourControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private TourRepository tourRepository;

    @MockitoBean
    private MockedStatic<SecurityUtil> mockedStatic;
    private ProfileEntity savedProfile;
    private AgencyEntity savedAgency;
    private static Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = ProfileEntity.builder()
                .role(Role.USER)
                .visible(true)
                .build();
        savedProfile = profileRepository.save(profile);

        AgencyEntity agency = AgencyEntity.builder()
                .id(savedProfile.getId())
                .ownerId(savedProfile.getId())
                .name("Qwerty")
                .phone("9999999999")
                .email("mail")
                .city("qwerty")
                .build();
        savedAgency = agencyRepository.save(agency);

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }
}