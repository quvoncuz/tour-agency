package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.entities.*;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.*;
import quvoncuz.service.PaymentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ProfileRepository profileRepository;
    private final TourRepository tourRepository;
    private final AgencyRepository agencyRepository;

    @Override
    public PaymentFullInfo processPayment(PaymentRequestDTO dto, Long userId) {

        List<ProfileEntity> profiles = profileRepository.findAll();
        ProfileEntity profile = profiles.stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<PaymentEntity> payments = paymentRepository.findAll();
        PaymentEntity payment = payments
                .stream()
                .filter(p -> p.getUserId().equals(userId)
                        && p.getTourId().equals(dto.getTourId())
                        && p.getBookingId().equals(dto.getBookingId())
                        && p.getStatus() == PaymentStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Payment not found for the given tour and booking"));

        List<BookingEntity> bookings = bookingRepository.findAll();
        BookingEntity booking = bookings.stream()
                .filter(b -> b.getId().equals(dto.getBookingId()) && b.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Booking not found for the given booking ID and user ID"));

        if (!profile.getIsActive()) {
            throw new DoNotMatchException("Profile is not active");
        }

        if (profile.getRole() != Role.USER) {
            throw new DoNotMatchException("Only users can make payments");
        }

        if (profile.getBalance().compareTo(payment.getAmount()) < 0) {
            throw new DoNotMatchException("Insufficient balance");
        }

        profile.setBalance(profile.getBalance() - payment.getAmount());
        payment.setStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);

        profileRepository.createOrReplace(profiles, false);
        paymentRepository.createOrUpdate(payments, false);
        bookingRepository.createOrUpdate(bookings, false);
        logger.info("Payment processed successfully for user ID: {}, tour ID: {}, booking ID: {}", userId, dto.getTourId(), dto.getBookingId());

        return PaymentMapper.toFullInfo(payment);
    }

    // ADMIN
    @Override
    public List<PaymentShortInfo> findAll(Long userId, int page, int size) {
        ProfileEntity profile = profileRepository.findById(userId);
        if (profile.getRole() != Role.ADMIN) {
            throw new DoNotMatchException("Only admins can access all payments");
        }
        logger.info("Admin requested all payments with pagination - page: {}, size: {}", page, size);
        return paymentRepository.findAll()
                .stream()
                // Sort by createdAt descending
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(PaymentMapper::toShortInfo)
                .toList();
    }

    // ADMIN and USER himself
    @Override
    public List<PaymentShortInfo> findAllByUserId(Long userId, int page, int size) {
        logger.info("User with ID: {} requested their payment history with pagination - page: {}, size: {}", userId, page, size);
        return paymentRepository.findAll()
                .stream()
                .filter(p -> p.getUserId().equals(userId))
                // Sort by createdAt descending
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(PaymentMapper::toShortInfo)
                .toList();
    }

    //ADMIN and AGENCY
    @Override
    public List<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size) {
        ProfileEntity admin = profileRepository.findById(userId);

        if (admin.getRole() == Role.ADMIN) {
            return paymentRepository.findAll()
                    .stream()
                    .filter(p -> p.getTourId().equals(tourId))
                    // Sort by createdAt descending
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .map(PaymentMapper::toShortInfo)
                    .toList();
        }

        logger.info("User with ID: {} requested payment history for tour ID: {} with pagination - page: {}, size: {}", userId, tourId, page, size);
        TourEntity tour = tourRepository.findById(tourId);
        AgencyEntity agency = agencyRepository.findById(tour.getAgencyId());
        return paymentRepository.findAll()
                .stream()
                .filter(p -> p.getTourId().equals(tourId)
                        && agency.getOwnerId().equals(userId))
                // Sort by createdAt descending
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(PaymentMapper::toShortInfo)
                .toList();
    }
}
