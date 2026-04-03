package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.entities.*;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.BookingMapper;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.*;
import quvoncuz.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final ProfileRepository profileRepository;
    private final PaymentRepository paymentRepository;
    private final AgencyRepository agencyRepository;


    @Override
    public BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId) {
        if (bookingRepository.existsByTourIdAndUserId(dto.getTourId(), userId)) {
            throw new AlreadyExistsException("Booking already exists for tourId: " + dto.getTourId() + " and userId: " + userId);
        }

        ProfileEntity profile = profileRepository.findById(userId);

        List<TourEntity> allTour = tourRepository.findAll();
        TourEntity tour = allTour
                .stream()
                .filter(t -> t.getId().equals(dto.getTourId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tour not found for tourId: " + dto.getTourId()));

        if (!tour.getStatus().equals(TourStatus.ACTIVE)) {
            throw new IllegalArgumentException("Tour is not active for tourId: " + dto.getTourId());
        }

        if (!profile.getIsActive()) {
            throw new IllegalArgumentException("User profile is not active for userId: " + userId);
        }

        if (tour.getAvailableSeats() < dto.getSeatsBooked()) {
            throw new IllegalArgumentException("Not enough available seats for tourId: " + dto.getTourId());
        }

        if (!tour.getIsActive()) {
            throw new IllegalArgumentException("Tour is not active for tourId: " + dto.getTourId());
        }


        tour.setAvailableSeats(tour.getAvailableSeats() - dto.getSeatsBooked());

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        tourRepository.createOrUpdate(allTour, false);

        BookingEntity booking = BookingMapper.toEntity(dto, userId);
        booking.setTotalPrice(tour.getPrice() * dto.getSeatsBooked());

        bookingRepository.createOrUpdate(List.of(booking), true);
        createPayment(booking.getId(), userId);
        logger.info("Booking created successfully for tourId: {} and userId: {}", dto.getTourId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public List<BookingShortInfo> findAllByUserId(Long userId, Long loginId, int page, int size) {
        ProfileEntity profile = profileRepository.findById(loginId);
        if (profile.getRole() != Role.ADMIN || !loginId.equals(userId)) {
            throw new IllegalArgumentException("Users can only view their own bookings. userId: " + userId + ", loginId: " + loginId);
        }
        logger.info("Finding all bookings for userId: {} with pagination - page: {}, size: {}", userId, page, size);
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .sorted((b1, b2) -> b2.getBookedAt().compareTo(b1.getBookedAt()))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(BookingMapper::toShortInfo)
                .toList();
    }

    @Override
    public List<BookingShortInfo> findAllByTourId(Long tourId, Long loginId, int page, int size) {
        logger.info("Finding all bookings for tourId: {} with pagination - page: {}, size: {}", tourId, page, size);
        ProfileEntity profile = profileRepository.findById(loginId);
        List<TourEntity> tourByAgencyId = tourRepository.findAll()
                .stream()
                .filter(tour -> tour.getAgencyId().equals(loginId))
                .toList();

        List<BookingEntity> allBooking = bookingRepository.findAll();
        return allBooking
                .stream()
                .filter(booking -> booking.getTourId().equals(tourId)
                        && (profile.getRole() == Role.ADMIN
                        || booking.getUserId().equals(loginId)
                        || tourByAgencyId.stream()
                        .anyMatch(tour -> tour.getId().equals(booking.getTourId()))))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(BookingMapper::toShortInfo)
                .toList();
    }

    @Override
    public List<BookingShortInfo> findAllByAgencyId(Long agencyId, Long loginId, int page, int size) {
        ProfileEntity profile = profileRepository.findById(loginId);
        AgencyEntity agency = agencyRepository.findById(agencyId);
        if (!(agency.getOwnerId().equals(loginId) || profile.getRole().equals(Role.ADMIN))) {
            throw new IllegalArgumentException("Only the agency owner or admin can view bookings for this agency. agencyId: " + agencyId + ", loginId: " + loginId);
        }
        List<TourEntity> tourByAgencyId = tourRepository.findAll()
                .stream()
                .filter(tour -> tour.getAgencyId().equals(agencyId))
                .toList();
        logger.info("Finding all bookings for agencyId: {} with pagination - page: {}, size: {}", loginId, page, size);
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> tourByAgencyId.stream()
                        .anyMatch(tour -> tour.getId().equals(booking.getTourId())))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(BookingMapper::toShortInfo)
                .toList();
    }

    @Override
    public boolean cancelBooking(Long bookingId, Long userId) {
        List<ProfileEntity> allProfile = profileRepository.findAll();
        ProfileEntity profile = allProfile.stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User profile not found for userId): " + userId));

        List<BookingEntity> allBooking = bookingRepository.findAll();
        BookingEntity booking = allBooking.stream()
                .filter(b -> b.getId().equals(bookingId) && b.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for bookingId: " + bookingId + " and userId: " + userId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalArgumentException("Booking is already canceled for bookingId: " + bookingId);
        }

        booking.setStatus(BookingStatus.CANCELED);
        if (booking.getPaidAmount() != null && booking.getPaidAmount() > 0) {
            profile.setBalance(profile.getBalance() + booking.getPaidAmount());
        }
        profileRepository.createOrReplace(allProfile, false);
        bookingRepository.createOrUpdate(allBooking, false);

        logger.info("Booking canceled successfully for bookingId: {} and userId: {}", bookingId, userId);
        return true;
    }

    @Override
    public BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId) {
        List<BookingEntity> allBooking = bookingRepository.findAll();
        BookingEntity booking = allBooking.stream()
                .filter(b -> b.getId().equals(bookingId) && b.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for userId: " + userId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalArgumentException("Cannot update seats for canceled booking for userId: " + userId);
        }

        List<TourEntity> allTour = tourRepository.findAll();
        TourEntity tour = allTour
                .stream()
                .filter(t -> t.getId().equals(booking.getTourId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tour not found for tourId: " + booking.getTourId()));

        List<PaymentEntity> allPayment = paymentRepository.findAll();
        PaymentEntity payment = allPayment.stream()
                .filter(p -> p.getBookingId().equals(booking.getId())
                        && p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (tour.getStatus() != TourStatus.ACTIVE) {
            throw new IllegalArgumentException("Tour is not active for tourId: " + tour.getId());
        }

        int seatsDifference = dto.getSeats() - booking.getSeatsBooked();

        if (tour.getAvailableSeats() < seatsDifference) {
            throw new IllegalArgumentException("Not enough available seats for tourId: " + tour.getId());
        }

        tour.setAvailableSeats(tour.getAvailableSeats() - seatsDifference);

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            payment.setAmount(tour.getPrice() * dto.getSeats());
            paymentRepository.createOrUpdate(allPayment, false);
        } else {
            if (seatsDifference < 0) {
                throw new IllegalArgumentException("Cannot reduce the number of seats booked for tourId: " + tour.getId());
            }
            PaymentEntity newPayment = PaymentMapper.toEntity(new PaymentRequestDTO(tour.getId(), booking.getId()), userId);
            newPayment.setAmount(tour.getPrice() * seatsDifference);
            paymentRepository.createOrUpdate(List.of(newPayment), true);
        }


        booking.setSeatsBooked(dto.getSeats());
        booking.setTotalPrice(tour.getPrice() * dto.getSeats());
        bookingRepository.createOrUpdate(allBooking, false);

        tourRepository.createOrUpdate(allTour, false);

        logger.info("Booking seats updated successfully for bookingId: {} and userId: {}", booking.getId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    private void createPayment(Long bookingId, Long userId) {
        BookingEntity booking = bookingRepository.findById(bookingId);
        PaymentEntity payment = PaymentEntity.builder()
                .id(null)
                .userId(userId)
                .tourId(booking.getTourId())
                .bookingId(bookingId)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.createOrUpdate(List.of(payment), true);
    }
}
