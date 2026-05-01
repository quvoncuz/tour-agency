package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.booking.*;

import java.util.List;

public interface BookingService {

    BookingFullInfo createBooking(CreateBookingRequestDTO dto);

    Page<BookingShortInfo> findAllByUserId(Long adminId, int page, int size);

    Page<BookingShortInfo> findAllByTourId(Long userId, int page, int size);

    Page<BookingShortInfo> findAllByAgencyId(Long userId, int page, int size);

    BookingFullInfo confirmUpdatedBooking(Long bookingId);

    BookingFullInfo cancelUpdateBooking(Long bookingId);

    boolean cancelBooking(CancelBookingRequestDTO dto);

    BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto);

    BookingFullInfo findFullInfoById(long bookingId);

    List<BookingFullInfo> getUpdatedBooking();

    Page<BookingShortInfo> findAllByUser(int page, int size);
}
