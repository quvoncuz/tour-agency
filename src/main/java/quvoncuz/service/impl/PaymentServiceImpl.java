package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.PaymentStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.PaymentMapper;
import quvoncuz.repository.PaymentRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.PaymentService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final ProfileRepository profileRepository;
    private final TourRepository tourRepository;


    @Override
    public Page<PaymentShortInfo> findAllByRefund(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return paymentRepository.findAllByStatusOrderByCreatedAtDesc(PaymentStatus.REFUND, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }

    // ADMIN
    @Override
    public Page<PaymentShortInfo> findAll(Long userId, int page, int size) {
        ProfileEntity profile = profileRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        if (profile.getRole() != Role.ADMIN) {
            throw new PermissionDeniedException("You don't have permission");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<PaymentEntity> pageResult = paymentRepository.findAll(pageRequest);

        logger.info("Admin requested all payment");
        return pageResult
                .map(PaymentMapper::toShortInfo);
    }

    // ADMIN and USER himself
    @Override
    public Page<PaymentShortInfo> findAllByUserId(Long userId, int page, int size) {

        logger.info("User with ID: {} requested their payment history", userId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return paymentRepository.findAllByUserId(userId, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }

    //ADMIN and AGENCY
    @Override
    public Page<PaymentShortInfo> findAllByTourId(Long tourId, Long userId, int page, int size) {
        ProfileEntity admin = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));
        AgencyEntity agency = tour.getAgency();
        if (!agency.getOwnerId().equals(userId) && admin.getRole() != Role.ADMIN) {
            throw new PermissionDeniedException("You don't have permission");
        }
        logger.info("User with ID: {} requested payment history for tour ID: {} ", userId, tourId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return paymentRepository.findAllByTourId(tourId, pageRequest)
                .map(PaymentMapper::toShortInfo);
    }
}
