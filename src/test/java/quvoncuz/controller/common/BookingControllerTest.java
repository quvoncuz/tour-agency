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
import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.BookingService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private static final Long USER_ID = 1L;
    private static final Long TOUR_ID = 1L;
    private static final Long BOOKING_ID = 1L;

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
    @WithMockUser(roles = {"USER", "AGENCY"})
    void createBooking_Success() throws Exception {

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(4);
        dto.setNote("To EUROPE");

        BookingFullInfo bookingFullInfo = new BookingFullInfo();
        bookingFullInfo.setId(BOOKING_ID);
        bookingFullInfo.setTotalPrice(400L);
        bookingFullInfo.setTourId(TOUR_ID);

        when(bookingService.createBooking(any(CreateBookingRequestDTO.class), eq(USER_ID)))
                .thenReturn(bookingFullInfo);

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.totalPrice").value(400L));

        verify(bookingService, times(1)).createBooking(any(), eq(USER_ID));

    }

    @Test
    @WithMockUser(roles = {"USER", "AGENCY"})
    void updateBookingSeats_Success() throws Exception {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(BOOKING_ID);
        dto.setSeats(6);

        BookingFullInfo bookingFullInfo = new BookingFullInfo();
        bookingFullInfo.setId(BOOKING_ID);
        bookingFullInfo.setTotalPrice(600L);
        bookingFullInfo.setTourId(TOUR_ID);

        when(bookingService.updateBookingSeats(BOOKING_ID, dto, USER_ID))
                .thenReturn(bookingFullInfo);

        mockMvc.perform(put("/bookings/{id}", BOOKING_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.data.totalPrice").value(600L));

        verify(bookingService, times(1)).updateBookingSeats(eq(BOOKING_ID), any(), eq(USER_ID));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBookingSeats_403Forbidden_ThrowsException() throws Exception {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(BOOKING_ID);
        dto.setSeats(6);

        mockMvc.perform(put("/bookings/{id}", BOOKING_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookingService);

    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_Success() throws Exception {
        BookingFullInfo fullInfo = new BookingFullInfo();
        fullInfo.setId(BOOKING_ID);

        when(bookingService.findFullInfoById(BOOKING_ID, USER_ID)).thenReturn(fullInfo);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(BOOKING_ID));

        verify(bookingService, times(1)).findFullInfoById(BOOKING_ID, USER_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ForbiddenRole_Returns() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookingService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_NegativeId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/-5"))
                .andExpect(status().isBadRequest());
    }


}