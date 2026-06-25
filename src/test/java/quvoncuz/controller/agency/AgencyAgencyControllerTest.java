package quvoncuz.controller.agency;

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
import quvoncuz.dto.agency.AgencyFullInfo;
import quvoncuz.dto.agency.UpdateAgencyRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgencyAgencyController.class)
class AgencyAgencyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private AgencyService agencyService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static final Long USER_ID = 1L;
    private static final Long AGENCY_ID = 1L;

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
    @WithMockUser(roles = "AGENCY")
    void update_Success() throws Exception {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("email@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");

        AgencyFullInfo fullInfo = AgencyFullInfo.builder()
                .name("EURO Tour")
                .email("email@gmail.com")
                .build();

        when(agencyService.update(anyLong(), anyLong(), any())).thenReturn(fullInfo);

        mockMvc.perform(put("/agency/agencies/{agencyId}", AGENCY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("EURO Tour"))
                .andExpect(jsonPath("$.data.email").value("email@gmail.com"));

        verify(agencyService, times(1)).update(anyLong(), anyLong(), any());
    }

    @Test
    void update_UnAuthorized_Throws() throws Exception {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("email@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");


        mockMvc.perform(put("/agency/agencies/{agencyId}", AGENCY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(agencyService);
    }


    @Test
    @WithMockUser(roles = "AGENCY")
    void update_ValidationFail() throws Exception {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("email@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");

        mockMvc.perform(put("/agency/agencies/{agencyId}", -1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(agencyService);
    }
}