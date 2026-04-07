package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByBookingIdAndUserIdOrderByCreatedAtDesc(Long bookingId, Long userId);

    Optional<PaymentEntity> findByUserIdAndTourIdAndBookingIdAndStatusIs(Long userId, Long tourId, Long bookingId, PaymentStatus paymentStatus);

    Page<PaymentEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<PaymentEntity> findAllByTourId(Long tourId, Pageable pageable);
}
