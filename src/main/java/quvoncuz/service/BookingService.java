package quvoncuz.service;

import quvoncuz.dto.booking.*;

import java.util.List;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    List<BookingShortInfo> findAllByUserId(Long adminId, Long userId, int page, int size);

    List<BookingShortInfo> findAllByTourId(Long userId, Long adminId, int page, int size);

    List<BookingShortInfo> findAllByAgencyId(Long userId, Long adminId, int page, int size);

    BookingFullInfo confirmUpdatedBooking(Long bookingId, Long userId);

    BookingFullInfo cancelUpdateBooking(Long bookingId, Long userId);

    boolean cancelBooking(CancelBookingRequestDTO dto, Long userId);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId);

    BookingFullInfo findById(long bookingId, long userId);

    List<BookingFullInfo> getUpdatedBooking(long userId);
}
