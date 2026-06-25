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
import quvoncuz.dto.agency.AgencyDTO;
import quvoncuz.dto.agency.CreateAgencyRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgencyController.class)
class AgencyControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgencyService agencyService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private static final Long USER_ID = 1L;
    private static final Long AGENCY_ID = 1L;

    @BeforeEach
    void setUp() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void applyForAgencies_Success() throws Exception {
        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setEmail("eurotour@mail.ru");
        dto.setPhone("99899099899");
        dto.setDescription("qefgbteb");
        dto.setCity("dgehdz");
        dto.setAddress("fjernjigneri");

        AgencyDTO agencyDTO = new AgencyDTO();
        agencyDTO.setId(AGENCY_ID);
        agencyDTO.setEmail("eurotour@mail.ru");
        agencyDTO.setOwnerId(USER_ID);

        when(agencyService.applyForAgency(dto, USER_ID)).thenReturn(agencyDTO);

        mockMvc.perform(post("/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ownerId").value(USER_ID))
                .andExpect(jsonPath("$.data.email").value("eurotour@mail.ru"));

        verify(agencyService, times(1)).applyForAgency(any(), eq(USER_ID));

    }

    @Test
    @WithMockUser(roles = {"ADMIN", "AGENCY"})
    void applyForAgencies_Forbidden_ThrowsException() throws Exception {
        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setEmail("eurotour@mail.ru");
        dto.setPhone("99899099899");
        dto.setDescription("qefgbteb");
        dto.setCity("dgehdz");
        dto.setAddress("fjernjigneri");

        mockMvc.perform(post("/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(agencyService);
    }

    @Test
    @WithMockUser
    void findById_Success() throws Exception {
        AgencyDTO agencyDTO = new AgencyDTO();
        agencyDTO.setId(AGENCY_ID);
        agencyDTO.setEmail("eurotour@mail.ru");
        agencyDTO.setOwnerId(USER_ID);

        when(agencyService.findByAgencyId(AGENCY_ID)).thenReturn(agencyDTO);

        mockMvc.perform(get("/agencies/{agencyId}", AGENCY_ID))
                .andExpect(status().isOk());

        verify(agencyService, times(1)).findByAgencyId(eq(AGENCY_ID));
    }
}