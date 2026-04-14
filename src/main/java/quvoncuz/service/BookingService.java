package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.booking.*;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    Page<BookingShortInfo> findAllByUserId(Long adminId, Long userId, int page, int size);

    Page<BookingShortInfo> findAllByTourId(Long userId, Long adminId, int page, int size);

    Page<BookingShortInfo> findAllByAgencyId(Long userId, Long adminId, int page, int size);

    boolean cancelBooking(CancelBookingRequestDTO dto, Long userId);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId);
}
