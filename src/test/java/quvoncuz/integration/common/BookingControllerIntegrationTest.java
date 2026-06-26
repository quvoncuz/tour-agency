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
import quvoncuz.dto.booking.CancelBookingRequestDTO;
import quvoncuz.dto.booking.ConfirmBookingDTO;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private MockedStatic<SecurityUtil> mockedStatic;
    private ProfileEntity savedProfile;
    private TourEntity savedTour;

    private static Long USER_ID = 1L;
    private static Long TOUR_ID = 1L;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("user@mail.com");
        profile.setUsername("user");
        profile.setPassword("password");
        profile.setRole(Role.USER);
        profile.setIsActive(true);
        profile.setVisible(true);
        savedProfile = profileRepository.save(profile);
        USER_ID = savedProfile.getId();

        TourEntity tour = new TourEntity();
        tour.setStatus(TourStatus.ACTIVE);
        tour.setAvailableSeats(10);
        tour.setPrice(100L);
        tour.setStartDate(LocalDate.now().plusDays(5));
        tour.setVisible(true);
        savedTour = tourRepository.save(tour);
        TOUR_ID = savedTour.getId();

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId)
                .thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser(roles = {"USER", "AGENCY"})
    void createBooking_Success() throws Exception {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(10);
        dto.setNote("For fun");

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPrice").value(1000L));

        TourEntity tour = tourRepository.findById(TOUR_ID).orElseThrow();
        assertEquals(0, tour.getAvailableSeats());
        assertEquals(TourStatus.SOLD_OUT, tour.getStatus());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createBooking_AlreadyExists_Conflict() throws Exception {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(savedTour.getId());
        dto.setSeatsBooked(2);

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = {"USER", "AGENCY"})
    void createBooking_DoNotMatch_Throws() throws Exception {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(14);
        dto.setNote("For fun");

        TourEntity tour = tourRepository.findById(TOUR_ID).orElseThrow();

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not enough available seats"));

        tour.setStartDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tour is not active"));

    }

    @Test
    @WithMockUser
    void cancelBooking_Success() throws Exception {
        CancelBookingRequestDTO dto = new CancelBookingRequestDTO();
        dto.setBookingId(1L);
        dto.setCancelReason("Just kidding");

        BookingEntity booking = BookingEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .seatsBooked(5)
                .paidAmount(0L)
                .totalPrice(500L)
                .status(BookingStatus.PENDING)
                .note("For fun")
                .bookedAt(LocalDateTime.now())
                .visible(true)
                .build();
        bookingRepository.save(booking);

        PaymentEntity payment1 = PaymentEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .bookingId(1L)
                .amount(300L)
                .status(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment1);

        PaymentEntity payment2 = PaymentEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .bookingId(1L)
                .amount(200L)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment2);

        mockMvc.perform(delete("/bookings/{bookingId}", dto.getBookingId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());

        booking = bookingRepository.findById(dto.getBookingId()).orElseThrow();
        List<PaymentEntity> allByBookingIdAndUserIdOrderByCreatedAtDesc = paymentRepository.findAllByBookingIdAndUserIdOrderByCreatedAtDesc(dto.getBookingId(), USER_ID);
        assertEquals(PaymentStatus.FAILED, allByBookingIdAndUserIdOrderByCreatedAtDesc.get(0).getStatus());
        assertEquals(PaymentStatus.REFUND, allByBookingIdAndUserIdOrderByCreatedAtDesc.get(1).getStatus());
        assertEquals(BookingStatus.CANCELED, booking.getStatus());
    }

    @Test
    @WithMockUser
    void updateBookingSeats_Success() throws Exception {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(1);
        dto.setSeats(8);

        BookingEntity booking = BookingEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .seatsBooked(5)
                .paidAmount(0L)
                .totalPrice(500L)
                .status(BookingStatus.CONFIRMED)
                .note("For fun")
                .bookedAt(LocalDateTime.now())
                .visible(true)
                .build();
        bookingRepository.save(booking);

        PaymentEntity payment = PaymentEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .bookingId(1L)
                .amount(500L)
                .status(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        mockMvc.perform(put("/bookings/{bookingId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPrice").value(800L));

        List<PaymentEntity> allByBookingIdAndUserIdOrderByCreatedAtDesc = paymentRepository.findAllByBookingIdAndUserIdOrderByCreatedAtDesc(1L, USER_ID);
        assertEquals(PaymentStatus.PENDING, allByBookingIdAndUserIdOrderByCreatedAtDesc.get(0).getStatus());
    }

    @Test
    @WithMockUser
    void confirmUpdatedBooking_Success() throws Exception {

        ConfirmBookingDTO dto = new ConfirmBookingDTO();
        dto.setConfirm(false);

        BookingEntity booking = BookingEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .seatsBooked(5)
                .paidAmount(0L)
                .totalPrice(500L)
                .status(BookingStatus.ON_UPDATE)
                .note("For fun")
                .bookedAt(LocalDateTime.now())
                .visible(true)
                .build();
        bookingRepository.save(booking);

        PaymentEntity payment = PaymentEntity.builder()
                .userId(USER_ID)
                .tourId(TOUR_ID)
                .bookingId(1L)
                .amount(500L)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.data.status").value(BookingStatus.CANCELED));

        booking = bookingRepository.findById(1L).orElseThrow();

        assertEquals(BookingStatus.CANCELED, booking.getStatus());


    }
}