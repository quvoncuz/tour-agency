package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.entities.TourEntity;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {

    List<TourEntity> findAllByAgencyIdOrderByCreatedAtDesc(Long agencyId);

    @Modifying
    @Transactional
    @Query("update TourEntity t set t.rating = ?1 where t.id = ?2")
    void updateRating(double averageRating, Long sourceId);

    Page<TourEntity> findAllByIdIn(List<Long> ids, Pageable pageable);

    void deleteByIdAndAgencyId(Long id, Long agencyId);

    @Query("select t from TourEntity as t where lower(t.title) like ?1 or lower(t.description) like ?1 or lower(t.destination) like ?1 order by t.createdAt desc ")
    Page<TourEntity> findAllByQuery(String query, Pageable pageable);

    Page<TourEntity> findAllByAgencyId(Long agencyId, Pageable pageable);
}
