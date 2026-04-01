package quvoncuz.service;

import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.entities.BookingEntity;

import java.util.List;

public interface BookingService {

    public BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId);

    public List<BookingEntity> findAllByUserId(Long adminId, Long userId, int page, int size);

    public List<BookingEntity> findAllByTourId(Long userId, Long adminId, int page, int size);

    public List<BookingEntity> findAllByAgencyId(Long userId, Long adminId, int page, int size);

    public boolean cancelBooking(Long bookingId, Long userId);

    public BookingFullInfo updateBookingSeats(Integer seats, Long user);

    public void payment(Long bookingId, Long userId);
}
