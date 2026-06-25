package quvoncuz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import quvoncuz.dto.booking.*;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.impl.BookingServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private ProfileEntity profile;
    private TourEntity tour;
    private BookingEntity booking;
    private static final Long USER_ID = 2L;
    private static final Long AGENCY_ID = 1L;
    private static final Long TOUR_ID = 1L;
    private static final Long BOOKING_ID = 1L;

    @BeforeEach
    void setUp() {
        profile = new ProfileEntity();
        profile.setId(USER_ID);
        profile.setIsActive(true);
        profile.setRole(Role.USER);
        profile.setIsActive(true);

        tour = new TourEntity();
        tour.setId(TOUR_ID);
        tour.setPrice(100L);
        tour.setMaxSeats(10);
        tour.setAvailableSeats(10);
        tour.setAgencyId(AGENCY_ID);
        tour.setStatus(TourStatus.ACTIVE);
        tour.setStartDate(LocalDate.now().plusDays(10));

        booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setSeatsBooked(4);
        booking.setTotalPrice(400L);
        booking.setTourId(TOUR_ID);
        booking.setUserId(USER_ID);
        booking.setPaidAmount(0L);
    }

    @Test
    void createBooking_Success() {

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(4);
        dto.setNote("Travel");


        when(bookingRepository.existsByTourIdAndUserIdAndStatusIsNot(TOUR_ID, USER_ID, BookingStatus.CANCELED))
                .thenReturn(false);
        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingFullInfo booking = bookingService.createBooking(dto, USER_ID);

        assertNotNull(booking);
        assertEquals(4, booking.getSeatsBooked());
        assertEquals(6, tour.getAvailableSeats());
        assertEquals(400, booking.getTotalPrice());

        verify(bookingRepository, times(1)).existsByTourIdAndUserIdAndStatusIsNot(TOUR_ID, USER_ID, BookingStatus.CANCELED);
        verify(profileService, times(1)).findById(USER_ID);
        verify(tourRepository, times(1)).findByIdWithLock(TOUR_ID);
        verify(tourRepository, times(1)).save(tour);
        verify(bookingRepository, times(1)).save(any());
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void createBooking_DoNotMatch_ThrowsException_NotActiveUser() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(4);
        dto.setNote("Travel");

        profile.setIsActive(false);

        when(profileService.findById(USER_ID)).thenReturn(profile);

        assertThrows(DoNotMatchException.class, () -> bookingService.createBooking(dto, USER_ID));

        verify(profileService, times(1)).findById(USER_ID);
    }

    @Test
    void createBooking_DoNotMatch_ThrowsException_NotActiveTour() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(4);
        dto.setNote("Travel");

        tour.setStartDate(LocalDate.now().minusDays(1));

        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));

        DoNotMatchException exception = assertThrows(DoNotMatchException.class, () -> bookingService.createBooking(dto, USER_ID));
        assertEquals("Tour is not active", exception.getMessage());

        verify(profileService, times(1)).findById(USER_ID);
        verify(tourRepository, times(1)).findByIdWithLock(TOUR_ID);
    }

    @Test
    void createBooking_DoNotMatch_ThrowsException_NotEnoughSeats() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(TOUR_ID);
        dto.setSeatsBooked(16);
        dto.setNote("Travel");

        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));

        DoNotMatchException exception = assertThrows(DoNotMatchException.class, () -> bookingService.createBooking(dto, USER_ID));
        assertEquals("Not enough available seats", exception.getMessage());

        verify(profileService, times(1)).findById(USER_ID);
        verify(tourRepository, times(1)).findByIdWithLock(TOUR_ID);
    }

    @Test
    void findAllByUserId_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<BookingEntity> pageResult = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllByUserId(USER_ID, pageRequest)).thenReturn(pageResult);

        Page<BookingShortInfo> allByUserId = bookingService.findAllByUserId(USER_ID, 1, 10);

        assertNotNull(allByUserId);
        assertEquals(400L, allByUserId.getContent().get(0).getTotalPrice());

        verify(bookingRepository, times(1)).findAllByUserId(USER_ID, pageRequest);

    }

    @Test
    void findAllForAgency_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<BookingEntity> pageResult = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllByAgencyId(AGENCY_ID, pageRequest)).thenReturn(pageResult);

        Page<BookingShortInfo> allForAgency = bookingService.findAllForAgency(0L, AGENCY_ID, 1, 10);

        assertNotNull(allForAgency);
        assertEquals(1, allForAgency.getContent().size());

        verify(bookingRepository, times(1)).findAllByAgencyId(AGENCY_ID, pageRequest);
        verify(tourRepository, never()).findById(anyLong()); // 🟢 Umuman chaqirilmaganini tekshiramiz
        verify(bookingRepository, never()).findAllByTourId(anyLong(), any());
    }

    @Test
    void confirmUpdatedBooking_WithConfirm_Success() {

        ConfirmBookingDTO dto = new ConfirmBookingDTO();
        dto.setConfirm(true);

        PaymentEntity payment = new PaymentEntity();
        payment.setBookingId(BOOKING_ID);
        payment.setTourId(TOUR_ID);
        payment.setUserId(USER_ID);
        payment.setStatus(PaymentStatus.PENDING);

        booking.setStatus(BookingStatus.ON_UPDATE);
        booking.setTotalPrice(2000L);

        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(USER_ID, TOUR_ID, BOOKING_ID, PaymentStatus.PENDING))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingFullInfo bookingFullInfo = bookingService.confirmUpdatedBooking(BOOKING_ID, dto, USER_ID);

        assertNotNull(bookingFullInfo);
        assertEquals(BookingStatus.PENDING, bookingFullInfo.getStatus());
        assertEquals(2000L, bookingFullInfo.getTotalPrice());

        verify(profileService, times(1)).findById(USER_ID);
        verify(bookingRepository, times(1)).findById(BOOKING_ID);
        verify(tourRepository, never()).save(any());
        verify(bookingRepository, times(1)).save(any());
        verify(paymentRepository, times(1)).findByUserIdAndTourIdAndBookingIdAndStatusIs(USER_ID, TOUR_ID, BOOKING_ID, PaymentStatus.PENDING);
        verify(paymentRepository, times(1)).save(any());

    }

    @Test
    void confirmUpdatedBooking_WithRejection_Success() {

        ConfirmBookingDTO dto = new ConfirmBookingDTO();
        dto.setConfirm(false);

        PaymentEntity payment = new PaymentEntity();
        payment.setBookingId(BOOKING_ID);
        payment.setTourId(TOUR_ID);
        payment.setUserId(USER_ID);
        payment.setStatus(PaymentStatus.PENDING);

        booking.setStatus(BookingStatus.ON_UPDATE);
        booking.setTotalPrice(2000L);

        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));
        when(tourRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(USER_ID, TOUR_ID, BOOKING_ID, PaymentStatus.PENDING))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingFullInfo bookingFullInfo = bookingService.confirmUpdatedBooking(BOOKING_ID, dto, USER_ID);

        assertNotNull(bookingFullInfo);
        assertEquals(BookingStatus.CANCELED, bookingFullInfo.getStatus());
        assertEquals(14, tour.getAvailableSeats());

        verify(profileService, times(1)).findById(USER_ID);
        verify(bookingRepository, times(1)).findById(BOOKING_ID);
        verify(tourRepository, times(1)).findByIdWithLock(TOUR_ID);
        verify(tourRepository, times(1)).save(any());
        verify(bookingRepository, times(1)).save(any());
        verify(paymentRepository, times(1)).findByUserIdAndTourIdAndBookingIdAndStatusIs(USER_ID, TOUR_ID, BOOKING_ID, PaymentStatus.PENDING);
    }

    @Test
    void confirmUpdatedBooking_WithWrongStatus() {
        ConfirmBookingDTO dto = new ConfirmBookingDTO();
        dto.setConfirm(false);

        booking.setStatus(BookingStatus.CONFIRMED);

        when(profileService.findById(USER_ID)).thenReturn(profile);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        DoNotMatchException exception = assertThrows(DoNotMatchException.class, () -> bookingService.confirmUpdatedBooking(BOOKING_ID, dto, USER_ID));
        assertEquals("Booking is not on update", exception.getMessage());

        verify(profileService, times(1)).findById(USER_ID);
        verify(bookingRepository, times(1)).findById(BOOKING_ID);
    }

    @Test
    void cancelBooking_Success() {
        CancelBookingRequestDTO dto = new CancelBookingRequestDTO();
        dto.setBookingId(BOOKING_ID);
        dto.setCancelReason("Just kidding");

        PaymentEntity payment = new PaymentEntity();
        payment.setBookingId(BOOKING_ID);
        payment.setTourId(TOUR_ID);
        payment.setUserId(USER_ID);
        payment.setStatus(PaymentStatus.PENDING);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));
        when(paymentRepository.findAllByBookingIdAndUserIdOrderByCreatedAtDesc(BOOKING_ID, USER_ID))
                .thenReturn(List.of(payment));

        bookingService.cancelBooking(dto, USER_ID);

        assertEquals("Just kidding", booking.getCancelReason());
        assertEquals(BookingStatus.CANCELED, booking.getStatus());
        assertEquals(14, tour.getAvailableSeats());
    }

    @Test
    void updateBookingSeats_Success() {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(BOOKING_ID);
        dto.setSeats(6);

        PaymentEntity payment = new PaymentEntity();
        payment.setBookingId(BOOKING_ID);
        payment.setTourId(TOUR_ID);
        payment.setUserId(USER_ID);
        payment.setStatus(PaymentStatus.PENDING);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(tourRepository.findByIdWithLock(TOUR_ID)).thenReturn(Optional.of(tour));
        when(paymentRepository.findAllByBookingIdAndUserIdOrderByCreatedAtDesc(BOOKING_ID, USER_ID))
                .thenReturn(List.of(payment));

        BookingFullInfo bookingFullInfo = bookingService.updateBookingSeats(BOOKING_ID, dto, USER_ID);

        assertNotNull(bookingFullInfo);
        assertEquals(6, bookingFullInfo.getSeatsBooked());
        assertEquals(8, tour.getAvailableSeats());
        assertEquals(600L, bookingFullInfo.getTotalPrice());

        verify(bookingRepository, times(1)).findById(BOOKING_ID);
        verify(tourRepository, times(1)).findByIdWithLock(TOUR_ID);
        verify(paymentRepository, times(1)).findAllByBookingIdAndUserIdOrderByCreatedAtDesc(BOOKING_ID, USER_ID);
        verify(paymentRepository, times(1)).save(any());
        verify(bookingRepository, times(1)).save(any());
        verify(tourRepository, times(1)).save(any());

    }

}