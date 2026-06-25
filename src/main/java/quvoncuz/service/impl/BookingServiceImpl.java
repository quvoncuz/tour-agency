package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.booking.*;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.BookingMapper;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.BookingService;
import quvoncuz.service.ProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final ProfileService profileService;
    private final PaymentRepository paymentRepository;

    /// ///////////////////////////// MANAGE
    @Override
    @Transactional
    public BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId) {

        if (bookingRepository.existsByTourIdAndUserIdAndStatusIsNot(dto.getTourId(), userId, BookingStatus.CANCELED)) {
            throw new AlreadyExistsException("Booking already exists");
        }

        ProfileEntity profile = profileService.findById(userId);

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("User is not active");
        }
        // lock
        TourEntity tour = tourRepository.findByIdWithLock(dto.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found!"));

        if (!tour.getStatus().equals(TourStatus.ACTIVE)) {
            throw new DoNotMatchException("Tour is not active");
        }

        if (tour.getStartDate().isBefore(LocalDate.now())) {
            throw new DoNotMatchException("Tour is not active");
        }

        if (tour.getAvailableSeats() < dto.getSeatsBooked()) {
            throw new DoNotMatchException("Not enough available seats");
        }

        tour.setAvailableSeats(tour.getAvailableSeats() - dto.getSeatsBooked());

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        tourRepository.save(tour);

        BookingEntity booking = BookingMapper.toEntity(dto, userId);
        booking.setTotalPrice(tour.getPrice() * dto.getSeatsBooked());
        booking = bookingRepository.save(booking);

        createPayment(booking, userId);

        log.info("Booking created successfully for tourId: {} and userId: {}", dto.getTourId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public Page<BookingShortInfo> findAllByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<BookingEntity> pageResult = bookingRepository.findAllByUserId(userId, pageRequest);
        log.info("finding all bookings for userId: {}", userId);
        return pageResult
                .map(BookingMapper::toShortInfo);
    }

    @Override
    public Page<BookingShortInfo> findAllForAdmin(Long userId, Long tourId, Long agencyId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<BookingEntity> result;

        if (userId != 0) {
            result = bookingRepository.findAllByUserId(userId, pageRequest);

        } else if (tourId != 0) {
            result = bookingRepository.findAllByTourId(tourId, pageRequest);

        } else if (agencyId != 0) {
            result = bookingRepository.findAllByAgencyId(agencyId, pageRequest);

        } else {
            result = bookingRepository.findAll(pageRequest);
        }

        log.info("finding bookings for admin");

        return result.map(BookingMapper::toShortInfo);
    }

    @Override
    public Page<BookingShortInfo> findAllForAgency(Long tourId, Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<BookingEntity> pageResult;

        if (tourId != 0) {
            TourEntity tour = tourRepository.findById(tourId)
                    .orElseThrow(() -> new NotFoundException("Tour not found"));
            if (!tour.getAgencyId().equals(userId)) {
                throw new DoNotMatchException("You don't have permission");
            }
            pageResult = bookingRepository.findAllByTourId(tourId, pageRequest);
        } else {
            pageResult = bookingRepository.findAllByAgencyId(userId, pageRequest);
            log.info("finding all bookings for agency");
        }

        return pageResult
                .map(BookingMapper::toShortInfo);
    }

    /// ////////////// MANAGE
    @Override
    @Transactional
    public BookingFullInfo confirmUpdatedBooking(Long bookingId, ConfirmBookingDTO dto, Long userId) {

        ProfileEntity profile = profileService.findById(userId);
        BookingEntity booking = findEntityById(bookingId);

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("User is not active");
        }

        if (booking.getStatus() != BookingStatus.ON_UPDATE) {
            throw new DoNotMatchException("Booking is not on update");
        }

        if (!booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        if (dto.isConfirm()) {
            booking.setStatus(BookingStatus.PENDING);
        } else {
            booking.setStatus(BookingStatus.CANCELED);
            TourEntity tour = tourRepository.findByIdWithLock(booking.getTourId()).orElseThrow(() -> new NotFoundException("Tour not found"));

            tour.setAvailableSeats(tour.getAvailableSeats() + booking.getSeatsBooked());
            if (tour.getStatus() == TourStatus.SOLD_OUT) {
                tour.setStatus(TourStatus.ACTIVE);
            }
            tourRepository.save(tour);
        }

        bookingRepository.save(booking);

        paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(userId, booking.getTourId(), bookingId, PaymentStatus.PENDING)
                .ifPresentOrElse(payment -> {
                    payment.setAmount(booking.getTotalPrice() - booking.getPaidAmount());
                    paymentRepository.save(payment);
                }, () -> {
                    throw new NotFoundException("Payment not found");
                });

        return BookingMapper.toFullInfo(booking);
    }

    /// ////////////// MANAGE
    @Override
    @Transactional
    public void cancelBooking(CancelBookingRequestDTO dto, Long userId) {

        BookingEntity booking = findEntityById(dto.getBookingId());

        if (!booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new AlreadyExistsException("Booking is already canceled");
        }

        TourEntity tour = tourRepository.findByIdWithLock(booking.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        if (LocalDate.now().isEqual(tour.getStartDate()) || LocalDate.now().isAfter(tour.getStartDate())) {
            throw new DoNotMatchException("You cannot cancel started tour");
        }

        List<PaymentEntity> payments = paymentRepository.findAllByBookingIdAndUserIdOrderByCreatedAtDesc(booking.getId(), userId);

        if (payments.isEmpty()) {
            throw new NotFoundException("Payments not found");
        }

        payments.forEach(payment -> {
            switch (payment.getStatus()) {
                case PAID    -> payment.setStatus(PaymentStatus.REFUND);
                case PENDING -> payment.setStatus(PaymentStatus.FAILED);
            }
        });

        booking.setStatus(BookingStatus.CANCELED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelReason(dto.getCancelReason());

        tour.setAvailableSeats(tour.getAvailableSeats() + booking.getSeatsBooked());

        if (tour.getStatus() == TourStatus.SOLD_OUT) {
            tour.setStatus(TourStatus.ACTIVE);
        }

        tourRepository.save(tour);
        bookingRepository.save(booking);
        paymentRepository.saveAll(payments);

        log.info("Booking canceled successfully for bookingId: {} and userId: {}", dto.getBookingId(), userId);
    }

    /// /////////////// MANAGE
    @Override
    @Transactional
    public BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId) {

        if (dto.getSeats() <= 0) {
            throw new DoNotMatchException("Seats must be greater than 0");
        }

        BookingEntity booking = findEntityById(bookingId);

        if (!booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new DoNotMatchException("Cannot update seats for canceled booking");
        }

        TourEntity tour = tourRepository.findByIdWithLock(booking.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found!"));

        PaymentEntity payment = paymentRepository
                .findAllByBookingIdAndUserIdOrderByCreatedAtDesc(bookingId, userId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (tour.getStartDate().isBefore(LocalDate.now()) || tour.getStartDate().isEqual(LocalDate.now())) {
            throw new DoNotMatchException("You cannot update started tour");
        }

        if (tour.getStatus() != TourStatus.ACTIVE) {
            throw new DoNotMatchException("Tour is not active");
        }

        int seatsDifference = dto.getSeats() - booking.getSeatsBooked();

        if (tour.getAvailableSeats() < seatsDifference) {
            throw new DoNotMatchException("Not enough available seats");
        }

        tour.setAvailableSeats(tour.getAvailableSeats() - seatsDifference);

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        if (seatsDifference < 0 && tour.getStatus() == TourStatus.SOLD_OUT) {
            tour.setStatus(TourStatus.ACTIVE);
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            payment.setAmount(tour.getPrice() * dto.getSeats());
            paymentRepository.save(payment);
        } else {
            if (seatsDifference < 0) {
                throw new DoNotMatchException("Cannot reduce the booked seats");
            }
            PaymentEntity newPayment = PaymentEntity.builder()
                    .userId(userId)
                    .tourId(tour.getId())
                    .bookingId(bookingId)
                    .amount(tour.getPrice() * seatsDifference)
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(newPayment);
        }

        booking.setSeatsBooked(dto.getSeats());
        booking.setTotalPrice(tour.getPrice() * dto.getSeats());
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        tourRepository.save(tour);

        log.info("Booking seats updated successfully for bookingId: {} and userId: {}", booking.getId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public BookingFullInfo findFullInfoById(long bookingId, Long userId) {

        ProfileEntity profile = profileService.findById(userId);

        BookingEntity booking;

        if (profile.getRole() == Role.USER) {
            booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
        } else {
            booking = bookingRepository.findByIdAndTour_AgencyId(bookingId, profile.getId())
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
        }

        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public List<BookingFullInfo> getUpdatedBooking(Long userId) {

        return bookingRepository.findAllByUserIdAndStatus(userId, BookingStatus.ON_UPDATE)
                .stream()
                .map(BookingMapper::toFullInfo)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createPayment(BookingEntity booking, Long userId) {
        PaymentEntity payment = PaymentEntity.builder()
                .userId(userId)
                .tourId(booking.getTourId())
                .bookingId(booking.getId())
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
    }

    private BookingEntity findEntityById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
    }
}
