package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.BookingMapper;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.BookingService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final ProfileRepository profileRepository;
    private final PaymentRepository paymentRepository;


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
        booking.setTotalPrice(tour.getPrice().multiply(BigDecimal.valueOf(dto.getSeatsBooked())));

        PaymentEntity payment = PaymentMapper.toEntity(new PaymentRequestDTO(tour.getId(), booking.getId()), userId);
        paymentRepository.createOrUpdate(List.of(payment), true);

        bookingRepository.createOrUpdate(List.of(booking), true);
        return BookingMapper.toFullInfo(booking);

    }

    @Override
    public List<BookingEntity> findAllByUserId(Long userId, Long adminId, int page, int size) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
    public List<BookingEntity> findAllByTourId(Long userId, Long adminId, int page, int size) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getTourId().equals(userId))
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
    public List<BookingEntity> findAllByAgencyId(Long userId, Long adminId, int page, int size) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getTourId().equals(userId))
                .skip((long) (page - 1) * size)
                .limit(size)
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
        if (booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            profile.setBalance(profile.getBalance().add(booking.getPaidAmount()));
        }
        profileRepository.createOrReplace(allProfile, false);
        bookingRepository.createOrUpdate(allBooking, false);

        return true;
    }

    @Override
    public BookingFullInfo updateBookingSeats(Integer seats, Long user) {
        List<BookingEntity> allBooking = bookingRepository.findAll();
        BookingEntity booking = allBooking.stream()
                .filter(b -> b.getUserId().equals(user))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for userId: " + user));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalArgumentException("Cannot update seats for canceled booking for userId: " + user);
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
                        && p.getUserId().equals(user))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (tour.getStatus() != TourStatus.ACTIVE) {
            throw new IllegalArgumentException("Tour is not active for tourId: " + tour.getId());
        }

        int seatsDifference = seats - booking.getSeatsBooked();

        if (tour.getAvailableSeats() < seatsDifference) {
            throw new IllegalArgumentException("Not enough available seats for tourId: " + tour.getId());
        }

        tour.setAvailableSeats(tour.getAvailableSeats() - seatsDifference);

        if (tour.getAvailableSeats() == 0) {
            tour.setStatus(TourStatus.SOLD_OUT);
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            payment.setAmount(tour.getPrice().multiply(BigDecimal.valueOf(seats)));
            paymentRepository.createOrUpdate(allPayment, false);
        } else {
            PaymentEntity newPayment = PaymentMapper.toEntity(new PaymentRequestDTO(tour.getId(), booking.getId()), user);
            newPayment.setAmount(tour.getPrice().multiply(BigDecimal.valueOf(seatsDifference)));
            paymentRepository.createOrUpdate(List.of(newPayment), true);
        }


        booking.setSeatsBooked(seats);
        booking.setTotalPrice(tour.getPrice().multiply(BigDecimal.valueOf(seats)));
        bookingRepository.createOrUpdate(allBooking, false);

        tourRepository.createOrUpdate(allTour, false);

        return BookingMapper.toFullInfo(booking);
    }

    @Override
    public void payment(Long bookingId, Long userId) {
        List<BookingEntity> allBooking = bookingRepository.findAll();
    }
}
