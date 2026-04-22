package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    boolean existsByTourIdAndUserId(Long tourId, Long userId);

    Page<BookingEntity> findAllByTourId(Long tourId, Pageable pageable);

    Page<BookingEntity> findAllByTourIdIsIn(List<Long> tourIds, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update BookingEntity set status = ?1 where id = ?2")
    void updateStatus(BookingStatus bookingStatus, Long bookingId);

    List<BookingEntity> findAllByUserId(Long userId);

    boolean existsByTourIdAndUserIdAndStatusIs(Long tourId, Long userId, BookingStatus status);

    boolean existsByTourIdAndUserIdAndStatusIsNot(Long tourId, Long userId, BookingStatus status);

    Optional<BookingEntity> findByIdAndUserId(long bookingId, long userId);

    List<BookingEntity> findAllByTourIdAndStatus(Long tourId, BookingStatus bookingStatus);

    List<BookingEntity> findAllByUserIdAndStatus(Long userId, BookingStatus status);
}
