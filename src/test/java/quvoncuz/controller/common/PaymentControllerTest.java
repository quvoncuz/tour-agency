package quvoncuz.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.PaymentService;
import quvoncuz.util.SecurityUtil;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    MockedStatic<SecurityUtil> mockedStatic;
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
    @WithMockUser(roles = {"USER"})
    void findAllByUserId_Success() throws Exception {
        PaymentShortInfo shortInfo1 = PaymentShortInfo.builder()
                .id(1L)
                .tourId(1L)
                .bookingId(1L)
                .build();

        PaymentShortInfo shortInfo2 = PaymentShortInfo.builder()
                .id(2L)
                .tourId(2L)
                .bookingId(2L)
                .build();

        Page<PaymentShortInfo> result = new PageImpl<>(List.of(shortInfo1, shortInfo2));

        when(paymentService.findAllByUserId(eq(USER_ID), anyInt(), anyInt())).thenReturn(result);

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(paymentService, times(1)).findAllByUserId(eq(USER_ID), anyInt(), anyInt());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "AGENCY"})
    void findAllByUserId_Forbidden() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(paymentService);
    }

}