package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.booking.*;

import java.util.List;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    Page<BookingShortInfo> findAllByUserId(Long userId, int page, int size);

    Page<BookingShortInfo> findAllForAdmin(Long userId, Long tourId, Long agencyId, int page, int size);

    Page<BookingShortInfo> findAllForAgency(Long tourId, Long userId, int page, int size);

    BookingFullInfo confirmUpdatedBooking(Long bookingId, ConfirmBookingDTO dto, Long userId);

    void cancelBooking(CancelBookingRequestDTO dto, Long userId);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId);

    BookingFullInfo findFullInfoById(long bookingId, Long userId);

    List<BookingFullInfo> getUpdatedBooking(Long userId);
}
