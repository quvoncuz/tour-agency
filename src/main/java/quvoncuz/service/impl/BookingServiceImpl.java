package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.booking.*;
import quvoncuz.entities.*;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.BookingMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.BookingService;
import quvoncuz.service.ProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final ProfileService profileService;
    private final PaymentRepository paymentRepository;
    private final AgencyRepository agencyRepository;


    @Override
    @Transactional
    public BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId) {
        if (bookingRepository.existsByTourIdAndUserIdAndStatusIsNot(dto.getTourId(), userId, BookingStatus.CANCELED)) {
            throw new AlreadyExistsException("Booking already exists for tourId: " + dto.getTourId() + " and userId: " + userId);
        }

        ProfileEntity profile = profileService.findById(userId);

        TourEntity tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found!"));

        if (!tour.getStatus().equals(TourStatus.ACTIVE)) {
            throw new DoNotMatchException("Tour is not active");
        }

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("User is not active");
        }

        if (tour.getAvailableSeats() < dto.getSeatsBooked()) {
            throw new DoNotMatchException("Not enough available seats");
        }

        if (!tour.getIsActive()) {
            throw new DoNotMatchException("Tour is not active");
        }

        tour.setAvailableSeats(tour.getAvailableSeats() - dto.getSeatsBooked());

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        BookingEntity booking = BookingMapper.toEntity(dto, userId);
        booking.setTotalPrice(tour.getPrice() * dto.getSeatsBooked());
        booking = bookingRepository.save(booking);

        createPayment(booking.getId(), userId);

        tourRepository.save(tour);
        logger.info("Booking created successfully for tourId: {} and userId: {}", dto.getTourId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingShortInfo> findAllByUserId(Long userId, Long loginId, int page, int size) {
        ProfileEntity profile = profileService.findById(loginId);
        if (profile.getRole() != Role.ADMIN && !loginId.equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }
        List<BookingEntity> pageResult = bookingRepository.findAll(page - 1, size);
        logger.info("Finding all bookings for userId: {}", userId);
        return pageResult
                .stream()
                .map(BookingMapper::toShortInfo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingShortInfo> findAllByTourId(Long tourId, Long loginId, int page, int size) {
        logger.info("Finding all bookings for tourId: {}", tourId);
        ProfileEntity profile = profileService.findById(loginId);
        List<BookingEntity> pageResult = bookingRepository.findAllByTourId(tourId, page - 1, size);
        if (profile.getRole() == Role.ADMIN) {
            return pageResult
                    .stream()
                    .map(BookingMapper::toShortInfo)
                    .toList();
        } else if (profile.getRole() == Role.AGENCY) {
            TourEntity tour = tourRepository.findById(tourId)
                    .orElseThrow(() -> new NotFoundException("Tour not found"));
            if (tour.getAgencyId().equals(loginId)) {
                return pageResult
                        .stream()
                        .map(BookingMapper::toShortInfo)
                        .toList();
            } else throw new DoNotMatchException("You don't have permission");
        } else {
            return pageResult
                    .stream()
                    .filter(bookingEntity -> bookingEntity.getUserId().equals(loginId))
                    .map(BookingMapper::toShortInfo)
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingShortInfo> findAllByAgencyId(Long agencyId, Long loginId, int page, int size) {
        ProfileEntity profile = profileService.findById(loginId);
        AgencyEntity agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!(agency.getOwnerId().equals(loginId) || profile.getRole().equals(Role.ADMIN))) {
            throw new PermissionDeniedException("You don't have permission");
        }

        List<Long> tourByAgencyId = tourRepository.findAllByAgencyId(agencyId)
                .stream()
                .map(TourEntity::getId)
                .toList();
        logger.info("Finding all bookings for agencyId: {}", loginId);
        return bookingRepository.findAllByTourIdIsIn(tourByAgencyId, page - 1, size)
                .stream()
                .map(BookingMapper::toShortInfo)
                .toList();
    }

    @Override
    @Transactional
    public BookingFullInfo confirmUpdatedBooking(Long bookingId, Long userId){
        ProfileEntity profile = profileService.findById(userId);
        BookingEntity booking = findById(bookingId);

        if (booking.getStatus() != BookingStatus.ON_UPDATE) {
            throw new DoNotMatchException("Booking is not on update");
        }

        if (profile.getRole() != Role.USER && !booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("User is not active");
        }

        if (!booking.getUserId().equals(userId)){
            throw new DoNotMatchException("You don't have permission");
        }

        booking.setStatus(BookingStatus.PENDING);

        paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(userId, booking.getTourId(), bookingId, PaymentStatus.PENDING)
                .ifPresentOrElse(payment -> {
                    payment.setAmount(booking.getTotalPrice() - booking.getPaidAmount());
                    paymentRepository.save(payment);
                }, () -> {
                    throw new NotFoundException("Payment not found");
                });

        return BookingMapper.toFullInfo(booking);
    }

    @Override
    @Transactional
    public BookingFullInfo cancelUpdateBooking(Long bookingId, Long userId){
        ProfileEntity profile = profileService.findById(userId);
        BookingEntity booking = findById(bookingId);

        if (booking.getStatus() != BookingStatus.ON_UPDATE) {
            throw new DoNotMatchException("Booking is not on update");
        }

        if (profile.getRole() != Role.USER && !booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("User is not active");
        }

        if (!booking.getUserId().equals(userId)){
            throw new DoNotMatchException("You don't have permission");
        }

        booking.setStatus(BookingStatus.CANCELED);

         paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(userId, booking.getTourId(), bookingId, PaymentStatus.PENDING)
                .ifPresentOrElse(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                }, () -> {
                    throw new NotFoundException("Payment not found");
                });

        return BookingMapper.toFullInfo(booking);
    }

    @Override
    @Transactional
    public boolean cancelBooking(CancelBookingRequestDTO dto, Long userId) {
        BookingEntity booking = findById(dto.getBookingId());

        TourEntity tour = tourRepository.findById(booking.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        List<PaymentEntity> payments = paymentRepository.findByBookingIdAndUserIdOrderByCreatedAtDesc(booking.getId(), userId);

        if (LocalDate.now().isEqual(tour.getStartDate()) || LocalDate.now().isAfter(tour.getStartDate())) {
            throw new DoNotMatchException("You cannot cancel started tour");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new AlreadyExistsException("Booking is already canceled");
        }

        if (!booking.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }

        payments.forEach(payment -> {
            if (payment.getStatus() == PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.REFUND);
            } else payment.setStatus(PaymentStatus.FAILED);
            payment.setCancelledAt(LocalDateTime.now());
        });

        booking.setStatus(BookingStatus.CANCELED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelReason(dto.getCancelReason());

        tour.setAvailableSeats(tour.getAvailableSeats() + booking.getSeatsBooked());

        tourRepository.save(tour);
        bookingRepository.save(booking);
        paymentRepository.saveAll(payments);

        logger.info("Booking canceled successfully for bookingId: {} and userId: {}", dto.getBookingId(), userId);
        return true;
    }

    @Override
    @Transactional
    public BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId) {
        BookingEntity booking = findById(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new DoNotMatchException("Cannot update seats for canceled booking");
        }

        TourEntity tour = tourRepository.findById(booking.getTourId())
                .orElseThrow(() -> new NotFoundException("Tour not found!"));

        PaymentEntity payment = paymentRepository.findByBookingIdAndUserIdOrderByCreatedAtDesc(bookingId, userId).get(0);

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
                    .amount(0L)
                    .status(PaymentStatus.PENDING)
                    .build();
            newPayment.setAmount(tour.getPrice() * seatsDifference);
            paymentRepository.save(newPayment);
        }

        booking.setSeatsBooked(dto.getSeats());
        booking.setTotalPrice(tour.getPrice() * dto.getSeats());
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        tourRepository.save(tour);

        logger.info("Booking seats updated successfully for bookingId: {} and userId: {}", booking.getId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingFullInfo findById(long bookingId, long userId) {
        BookingEntity booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingFullInfo> getUpdatedBooking(long userId) {
        return bookingRepository.findAllUpdated(userId)
                .stream()
                .map(BookingMapper::toFullInfo)
                .toList();
    }

    private void createPayment(Long bookingId, Long userId) {
        BookingEntity booking = findById(bookingId);
        PaymentEntity payment = PaymentEntity.builder()
                .userId(userId)
                .tourId(booking.getTourId())
                .bookingId(bookingId)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
    }

    private BookingEntity findById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
    }
}
