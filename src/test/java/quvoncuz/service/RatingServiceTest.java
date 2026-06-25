package quvoncuz.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.RatingType;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.RatingRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.impl.RatingServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private static final Long USER_ID = 1L;
    private static final Long SOURCE_ID = 1L;
    private static final Long RATING_ID = 1L;

    @Test
    void create_ForAgency_Success() {
        BookingEntity booking = new BookingEntity();
        booking.setStatus(BookingStatus.CONFIRMED);

        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(SOURCE_ID);
        dto.setType(RatingType.AGENCY);
        dto.setComment("Good");
        dto.setStars(4);

        when(ratingRepository.findByUserIdAndSourceIdAndType(USER_ID, SOURCE_ID, RatingType.AGENCY))
                .thenReturn(Optional.empty());
        when(agencyRepository.existsById(SOURCE_ID)).thenReturn(true);
        when(bookingRepository.findAllByUserIdAndTour_AgencyId(USER_ID, SOURCE_ID)).thenReturn(List.of(booking));
        when(ratingRepository.calculateAverageStars(SOURCE_ID, RatingType.AGENCY)).thenReturn(4.0d);

        RatingFullInfo ratingFullInfo = ratingService.create(dto, USER_ID);

        assertNotNull(ratingFullInfo);
        assertEquals("Good", ratingFullInfo.getComment());
        assertEquals(4, ratingFullInfo.getStars());

        verify(ratingRepository, times(1)).findByUserIdAndSourceIdAndType(USER_ID, SOURCE_ID, RatingType.AGENCY);
        verify(agencyRepository, times(1)).existsById(SOURCE_ID);
        verify(tourRepository, never()).existsById(SOURCE_ID);
        verify(bookingRepository, times(1)).findAllByUserIdAndTour_AgencyId(USER_ID, SOURCE_ID);
        verify(bookingRepository, never()).findAllByUserIdAndTourIdAndStatus(USER_ID, SOURCE_ID, BookingStatus.CONFIRMED);
        verify(ratingRepository, times(1)).save(any());
        verify(ratingRepository, times(1)).calculateAverageStars(SOURCE_ID, RatingType.AGENCY);
    }

    @Test
    void create_ForTour_Success() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(SOURCE_ID);
        dto.setType(RatingType.TOUR);
        dto.setComment("Good");
        dto.setStars(4);

        when(ratingRepository.findByUserIdAndSourceIdAndType(USER_ID, SOURCE_ID, RatingType.TOUR))
                .thenReturn(Optional.empty());
        when(tourRepository.existsById(SOURCE_ID)).thenReturn(true);
        when(bookingRepository.findAllByUserIdAndTourIdAndStatus(USER_ID, SOURCE_ID, BookingStatus.CONFIRMED)).thenReturn(Optional.of(new BookingEntity()));
        when(ratingRepository.calculateAverageStars(SOURCE_ID, RatingType.TOUR)).thenReturn(4.0d);

        RatingFullInfo ratingFullInfo = ratingService.create(dto, USER_ID);

        assertNotNull(ratingFullInfo);
        assertEquals("Good", ratingFullInfo.getComment());
        assertEquals(4, ratingFullInfo.getStars());

        verify(ratingRepository, times(1)).findByUserIdAndSourceIdAndType(USER_ID, SOURCE_ID, RatingType.TOUR);
        verify(tourRepository, times(1)).existsById(SOURCE_ID);
        verify(agencyRepository, never()).existsById(SOURCE_ID);
        verify(bookingRepository, never()).findAllByUserIdAndTour_AgencyId(USER_ID, SOURCE_ID);
        verify(bookingRepository, times(1)).findAllByUserIdAndTourIdAndStatus(USER_ID, SOURCE_ID, BookingStatus.CONFIRMED);
        verify(ratingRepository, times(1)).save(any());
        verify(ratingRepository, times(1)).calculateAverageStars(SOURCE_ID, RatingType.TOUR);
    }

    @Test
    void update_Success() {
        RatingEntity rating = new RatingEntity();
        rating.setId(RATING_ID);
        rating.setComment("Good");
        rating.setSourceId(SOURCE_ID);
        rating.setStars(4);
        rating.setUserId(USER_ID);

        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setComment("The best of ever seen");
        dto.setStars(5);

        when(ratingRepository.findById(RATING_ID)).thenReturn(Optional.of(rating));

        RatingFullInfo updated = ratingService.update(RATING_ID, dto, USER_ID);

        assertNotNull(updated);
        assertEquals(5, updated.getStars());
        assertEquals("The best of ever seen", updated.getComment());

        verify(ratingRepository, times(1)).findById(RATING_ID);
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void update_DoNotMatch_ThrowsException() {
        RatingEntity rating = new RatingEntity();
        rating.setId(RATING_ID);
        rating.setComment("Good");
        rating.setSourceId(SOURCE_ID);
        rating.setStars(4);
        rating.setUserId(USER_ID);

        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setComment("The best of ever seen");
        dto.setStars(5);

        when(ratingRepository.findById(RATING_ID)).thenReturn(Optional.of(rating));

        DoNotMatchException exception = assertThrows(DoNotMatchException.class, () -> ratingService.update(RATING_ID, dto, 2L));

        assertEquals("You don't have permission", exception.getMessage());

        verify(ratingRepository, times(1)).findById(RATING_ID);
    }
}