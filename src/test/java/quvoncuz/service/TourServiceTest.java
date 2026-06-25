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
import quvoncuz.dto.tour.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.impl.TourServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private AgencyService agencyService;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private TourServiceImpl tourService;

    private AgencyEntity agency;
    private TourEntity tour;
    private static final Long USER_ID = 1L;
    private static final Long TOUR_ID = 100L;
    private static final Long AGENCY_ID = 1L;

    @BeforeEach
    void setUp() {
        agency = new AgencyEntity();

        agency.setId(AGENCY_ID);
        agency.setApproved(true);
        agency.setVisible(true);
        agency.setStatus(AgencyStatus.ACCEPTED);
        agency.setOwnerId(USER_ID);
        agency.setName("Test Agency");

        tour = new TourEntity();
        tour.setId(TOUR_ID);
        tour.setAgencyId(AGENCY_ID);
        tour.setTitle("Samarkand Tour");
        tour.setMaxSeats(50);
        tour.setDurationDays(3);
        tour.setPrice(300L);

    }

    @Test
    void createTour_Success() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("Samarkand Tour");
        dto.setMaxSeats(50);
        dto.setDurationDays(3);
        dto.setPrice(300);



        // 2. Mock qoidalarini aniq yozish
        when(agencyRepository.findByOwnerId(USER_ID)).thenReturn(Optional.of(agency));

        when(tourRepository.save(any(TourEntity.class))).thenReturn(tour);

        TourFullInfo result = tourService.createTour(dto, USER_ID);

        assertNotNull(result);

        assertEquals(tour.getTitle(), result.getTitle());
        assertEquals(tour.getMaxSeats(), result.getMaxSeats());
        assertEquals(tour.getPrice(), result.getPrice());

        verify(tourRepository, times(1)).save(any(TourEntity.class));
    }

    @Test
    void createTour_AgencyNotFound_ThrowsException() {
        Long userId = 2L;
        CreateTourRequestDTO dto = new CreateTourRequestDTO();

        when(agencyRepository.findByOwnerId(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tourService.createTour(dto, userId));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void createTour_AgencyNotAccepted_ThrowsPermissionDenied() {
        Long userId = 3L;
        CreateTourRequestDTO dto = new CreateTourRequestDTO();

        AgencyEntity agencyEntity = new AgencyEntity();
        agencyEntity.setId(3L);
        agencyEntity.setStatus(AgencyStatus.PENDING);
        agencyEntity.setVisible(true);
        agencyEntity.setApproved(true);
        agencyEntity.setOwnerId(userId);
        agencyEntity.setName("Test Agency");

        when(agencyRepository.findByOwnerId(userId)).thenReturn(Optional.of(agencyEntity));

        assertThrows(PermissionDeniedException.class, () -> tourService.createTour(dto, userId));
    }

    @Test
    void updateTour_Success_WithPriceChange() {
        Long userId = 1L;

        UpdateTourRequestDTO dto = new UpdateTourRequestDTO();
        dto.setTitle("Updated Tashkent Tour");
        dto.setPrice(600L);

        BookingEntity booking = new BookingEntity();
        booking.setSeatsBooked(2);
        booking.setStatus(BookingStatus.PENDING);

        when(agencyRepository.findByOwnerId(userId)).thenReturn(Optional.of(agency));
        when(tourRepository.findById(TOUR_ID)).thenReturn(Optional.of(tour));
        when(bookingRepository.findAllByTourIdAndStatus(TOUR_ID, BookingStatus.PENDING)).thenReturn(List.of(booking));

        TourFullInfo result = tourService.updateTour(TOUR_ID, dto, userId);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(BookingStatus.ON_UPDATE, booking.getStatus());
        assertEquals(1200L, booking.getTotalPrice()); // 2 * 600,000 = 1,200,000
        verify(bookingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void cancelTour_Success() {
        CancelTourDTO reason = new CancelTourDTO();
        reason.setReason("Just kidding");

        TourEntity tour = new TourEntity();
        tour.setId(TOUR_ID);
        tour.setAgencyId(AGENCY_ID);
        tour.setStatus(TourStatus.ACTIVE);

        when(agencyService.findByOwnerId(USER_ID)).thenReturn(Optional.of(agency));
        when(tourRepository.findById(TOUR_ID)).thenReturn(Optional.of(tour));
        when(bookingRepository.findAllByTourId(TOUR_ID)).thenReturn(Collections.emptyList());

        tourService.cancelTour(TOUR_ID, reason, USER_ID);

        assertEquals(TourStatus.CANCELLED, tour.getStatus());
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void getAllActiveTour_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<TourEntity> page = new PageImpl<>(List.of(tour));

        when(tourRepository.findAllByStatusAndVisible(TourStatus.ACTIVE, true, pageRequest)).thenReturn(page);

        Page<TourShortInfo> result = tourService.getAllActiveTour(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Samarkand Tour", result.getContent().get(0).getTitle());
    }

    @Test
    void getById_Success_IncrementsViewCount() {

        when(tourRepository.findById(TOUR_ID)).thenReturn(Optional.of(tour));

        TourFullInfo result = tourService.getById(TOUR_ID);

        assertNotNull(result);
        verify(tourRepository, times(1)).incrementViewCount(TOUR_ID);
    }
}