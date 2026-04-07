package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ProfileRepository profileRepository;
    private final TourRepository tourRepository;
    private final AgencyRepository agencyRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentFullInfo processPayment(PaymentRequestDTO dto, Long userId) {

        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        PaymentEntity payment = paymentRepository.findByUserIdAndTourIdAndBookingIdAndStatusIs(userId, dto.getTourId(), dto.getBookingId(), PaymentStatus.PENDING).orElseThrow(() -> new NotFoundException("Payment not found"));

        BookingEntity booking = bookingRepository.findById(dto.getBookingId()).orElseThrow(() -> new NotFoundException("Booking not fount"));

        AgencyEntity agency = tourRepository.findById(dto.getTourId()).orElseThrow(() -> new NotFoundException("Tour not found")).getAgency();

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("This booking does not belong to the user");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Booking is not in pending state");
        }

        if (!profile.getIsActive()) {
            throw new IllegalArgumentException("Profile is not active");
        }

        if (profile.getRole() != Role.USER) {
            throw new IllegalArgumentException("Only users can make payments");
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        if (profile.getBalance() < payment.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        profile.setBalance(profile.getBalance() - payment.getAmount());
        payment.setStatus(PaymentStatus.PAID);

        agency.setAmount(agency.getAmount().add(BigDecimal.valueOf(payment.getAmount())));

        booking.setPaidAmount(payment.getAmount());
        booking.setStatus(BookingStatus.CONFIRMED);

        profileRepository.save(profile);
        paymentRepository.save(payment);
        bookingRepository.save(booking);
        agencyRepository.save(agency);

        logger.info("Payment processed successfully for user ID: {}, tour ID: {}, booking ID: {}", userId, dto.getTourId(), dto.getBookingId());

        return PaymentMapper.toFullInfo(payment);
    }

    // ADMIN
    @Override
    public Page<PaymentShortInfo> findAll(Long userId, int page, int size) {
        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        if (profile.getRole() != Role.ADMIN) {
            throw new DoNotMatchException("Only admins can access all payments");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PaymentEntity> pageResult = paymentRepository.findAll(pageRequest);

        logger.info("Admin requested all payments with pagination - page: {}, size: {}", page, size);
        return pageResult.map(PaymentMapper::toShortInfo);
    }

    // ADMIN and USER himself
    @Override
    public Page<PaymentShortInfo> findAllByUserId(Long userId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        logger.info("User with ID: {} requested their payment history with pagination - page: {}, size: {}", userId, page, size);
        return paymentRepository.findAllByUserId(userId, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }

    //ADMIN and AGENCY
    @Override
    public Page<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size) {
        ProfileEntity admin = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));
        AgencyEntity agency = tour.getAgency();
        if (!agency.getOwnerId().equals(userId) && admin.getRole() != Role.ADMIN) {
            throw new DoNotMatchException("You don't have permission");
        }
        logger.info("User with ID: {} requested payment history for tour ID: {} with pagination - page: {}, size: {}", userId, tourId, page, size);
        return paymentRepository.findAllByTourId(tourId, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }
}
