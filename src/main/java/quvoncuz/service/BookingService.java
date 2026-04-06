package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    Page<BookingShortInfo> findAllByUserId(Long adminId, Long userId, int page, int size);

    Page<BookingShortInfo> findAllByTourId(Long userId, Long adminId, int page, int size);

    Page<BookingShortInfo> findAllByAgencyId(Long userId, Long adminId, int page, int size);

    boolean cancelBooking(Long bookingId, Long userId);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId);
}
