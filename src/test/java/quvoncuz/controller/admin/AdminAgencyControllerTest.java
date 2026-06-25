package quvoncuz.controller.admin;

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
import quvoncuz.dto.agency.AgencyApproveRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAgencyController.class)
class AdminAgencyControllerTest {

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
    @WithMockUser(roles = {"ADMIN"})
    void approveAgency_Success() throws Exception {
        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(AGENCY_ID);
        dto.setApprove(true);

        String result = "Successfully accepted";

        mockMvc.perform(post("/admin/agencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(result));
    }
}