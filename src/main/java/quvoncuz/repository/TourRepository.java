package quvoncuz.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.TourStatus;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TourEntity t where t.id = ?1")
    Optional<TourEntity> findByIdWithLock(Long id);

    @Modifying
    @Transactional
    @Query("update TourEntity t set t.rating = ?1 where t.id = ?2")
    void updateRating(double averageRating, Long sourceId);


    @Query("select t from TourEntity t JOIN SavedTourEntity s ON s.tourId = t.id where s.userId = ?1 ORDER BY s.createdAt DESC")
    Page<TourEntity> findSavedToursByUserId(Long userId, Pageable pageable);

    @Query("select t from TourEntity as t where t.title ilike ?1 or t.description ilike ?1 or t.destination ilike ?1 order by t.createdAt desc ")
    Page<TourEntity> findAllByQuery(String query, Pageable pageable);

    Page<TourEntity> findAllByAgencyId(Long agencyId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update TourEntity t set t.visible = ?1 where t.id = ?2 and t.agencyId = ?3")
    void updateVisible(boolean visible, Long tourId, Long agencyId);

    @Modifying
    @Transactional
    @Query("update TourEntity t set t.visible = ?1 where t.agencyId = ?2")
    void updateVisibleByAgencyId(Long agencyId);

    Page<TourEntity> findAllByStatusAndVisible(TourStatus status, Boolean visible, Pageable pageable);

    @Modifying
    @Query("update TourEntity t set t.viewCount = t.viewCount + 1 where t.id = ?1")
    void incrementViewCount(Long id);
}
