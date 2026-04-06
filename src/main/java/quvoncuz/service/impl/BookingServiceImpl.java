package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.BookingMapper;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.*;
import quvoncuz.service.BookingService;
import quvoncuz.service.ProfileService;

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
    private final ProfileRepository profileRepository;


    @Override
    public BookingFullInfo createBooking(CreateBookingRequestDTO dto, Long userId) {
        if (bookingRepository.existsByTourIdAndUserId(dto.getTourId(), userId)) {
            throw new AlreadyExistsException("Booking already exists for tourId: " + dto.getTourId() + " and userId: " + userId);
        }

        ProfileEntity profile = profileService.findById(userId);

        TourEntity tour = tourRepository.findById(dto.getTourId()).orElseThrow(() -> new NotFoundException("Tour not found!"));

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

        tourRepository.save(tour);

        BookingEntity booking = BookingMapper.toEntity(dto, userId);
        booking.setTotalPrice(tour.getPrice() * dto.getSeatsBooked());

        bookingRepository.save(booking);
        createPayment(booking.getId(), userId);
        logger.info("Booking created successfully for tourId: {} and userId: {}", dto.getTourId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public Page<BookingShortInfo> findAllByUserId(Long userId, Long loginId, int page, int size) {
        ProfileEntity profile = profileService.findById(loginId);
        if (profile.getRole() != Role.ADMIN && !loginId.equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to view bookings for this user. userId: " + userId + ", loginId: " + loginId);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<BookingEntity> pageResult = bookingRepository.findAll(pageRequest);
        logger.info("Finding all bookings for userId: {} with pagination - page: {}, size: {}", userId, page, size);
        return pageResult.map(BookingMapper::toShortInfo);
    }

    @Override
    public Page<BookingShortInfo> findAllByTourId(Long tourId, Long loginId, int page, int size) {
        logger.info("Finding all bookings for tourId: {} with pagination - page: {}, size: {}", tourId, page, size);
        ProfileEntity profile = profileService.findById(loginId);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<BookingEntity> pageResult = bookingRepository.findAllByTourId(tourId, pageRequest);
        if (profile.getRole() == Role.ADMIN) {
            return pageResult.map(BookingMapper::toShortInfo);
        } else if (profile.getRole() == Role.AGENCY) {
            TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));
            if (tour.getAgency().getId().equals(loginId)) {
                return pageResult.map(BookingMapper::toShortInfo);
            } else throw new DoNotMatchException("You don't have permission");
        } else {
            List<BookingShortInfo> content = pageResult.getContent()
                    .stream()
                    .filter(bookingEntity -> bookingEntity.getUserId().equals(loginId))
                    .map(BookingMapper::toShortInfo)
                    .toList();
            return new PageImpl<>(content, pageRequest, pageResult.getTotalElements());
        }
    }

    @Override
    public Page<BookingShortInfo> findAllByAgencyId(Long agencyId, Long loginId, int page, int size) {
        ProfileEntity profile = profileService.findById(loginId);
        AgencyEntity agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!(agency.getOwnerId().equals(loginId) || profile.getRole().equals(Role.ADMIN))) {
            throw new IllegalArgumentException("Only the agency owner or admin can view bookings for this agency. agencyId: " + agencyId + ", loginId: " + loginId);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Long> tourByAgencyId = tourRepository.findAllByAgencyId(agencyId)
                .stream()
                .map(TourEntity::getId)
                .toList();
        logger.info("Finding all bookings for agencyId: {} with pagination - page: {}, size: {}", loginId, page, size);
        return bookingRepository.findAllByTourIdIsIn(tourByAgencyId, pageRequest).map(BookingMapper::toShortInfo);
    }

    @Override
    public boolean cancelBooking(Long bookingId, Long userId) {
        ProfileEntity profile = profileService.findById(userId);

        BookingEntity booking = findById(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalArgumentException("Booking is already canceled for bookingId: " + bookingId);
        }

        booking.setStatus(BookingStatus.CANCELED);
        if (booking.getPaidAmount() != null && booking.getPaidAmount() > 0) {
            profile.setBalance(profile.getBalance() + booking.getPaidAmount());
        }
        profileRepository.save(profile);
        bookingRepository.save(booking);

        logger.info("Booking canceled successfully for bookingId: {} and userId: {}", bookingId, userId);
        return true;
    }

    @Override
    public BookingFullInfo updateBookingSeats(Long bookingId, UpdateBookingRequestDTO dto, Long userId) {
        BookingEntity booking = findById(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalArgumentException("Cannot update seats for canceled booking for userId: " + userId);
        }

        TourEntity tour = tourRepository.findById(booking.getTourId()).orElseThrow(() -> new NotFoundException("Tour not found!"));

        PaymentEntity payment = paymentRepository.findByBookingIdAndUserIdOrderByCreatedAtDesc(bookingId, userId);

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
            paymentRepository.save(payment);
        } else {
            if (seatsDifference < 0) {
                throw new IllegalArgumentException("Cannot reduce the number of seats booked for tourId: " + tour.getId());
            }
            PaymentEntity newPayment = PaymentMapper.toEntity(new PaymentRequestDTO(tour.getId(), booking.getId()), userId);
            newPayment.setAmount(tour.getPrice() * seatsDifference);
            paymentRepository.save(newPayment);
        }


        booking.setSeatsBooked(dto.getSeats());
        booking.setTotalPrice(tour.getPrice() * dto.getSeats());
        bookingRepository.save(booking);

        tourRepository.save(tour);

        logger.info("Booking seats updated successfully for bookingId: {} and userId: {}", booking.getId(), userId);
        return BookingMapper.toFullInfo(booking);
    }

    private void createPayment(Long bookingId, Long userId) {
        BookingEntity booking = findById(bookingId);
        PaymentEntity payment = PaymentEntity.builder()
                .id(null)
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
