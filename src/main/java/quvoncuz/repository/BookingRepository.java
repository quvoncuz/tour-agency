package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findAllByTourId(Long tourId, Pageable pageable);

    List<BookingEntity> findAllByTourId(Long tourId);

    Page<BookingEntity> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByTourIdAndUserIdAndStatusIsNot(Long tourId, Long userId, BookingStatus status);

    Optional<BookingEntity> findByIdAndUserId(long bookingId, long userId);

    List<BookingEntity> findAllByTourIdAndStatus(Long tourId, BookingStatus bookingStatus);

    List<BookingEntity> findAllByUserIdAndStatus(Long userId, BookingStatus status);

    @Query("from BookingEntity as b where b.tour.agencyId = ?1 order by b.bookedAt desc ")
    Page<BookingEntity> findAllByAgencyId(long userId, Pageable pageable);

    @Query("select b.user.email from BookingEntity as b where b.tour.agencyId = ?1")
    List<String> findAllUserEmailBookedByAgency(Long agencyId);

    @Query("select b.user.email from BookingEntity as b where b.tourId = ?1 and b.status <> ?2")
    List<String> findAllEmailByTourIdAndStatus(Long tourId, BookingStatus bookingStatus);

    Optional<BookingEntity> findByIdAndTour_AgencyId(Long id, Long tourAgencyId);

    Optional<BookingEntity> findAllByUserIdAndTourIdAndStatus(Long userId, Long tourId, BookingStatus status);

    List<BookingEntity> findAllByUserIdAndTour_AgencyId(Long userId, Long tourAgencyId);
}
