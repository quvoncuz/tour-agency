package quvoncuz.service;

import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;

import java.util.List;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    List<BookingShortInfo> findAllByUserId(Long adminId, Long userId, int page, int size);

    List<BookingShortInfo> findAllByTourId(Long userId, Long adminId, int page, int size);

    List<BookingShortInfo> findAllByAgencyId(Long userId, Long adminId, int page, int size);

    boolean cancelBooking(Long bookingId, Long userId);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId);
}
