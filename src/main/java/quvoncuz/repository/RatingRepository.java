package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    Page<RatingEntity> findAllBySourceIdAndType(Long sourceId, RatingType type, Pageable pageable);

    @Query("SELECT AVG(r.stars) FROM RatingEntity r WHERE r.sourceId = ?1 AND r.type = ?2")
    Double calculateAverageStars(Long sourceId, RatingType type);

    void deleteByIdAndUserId(Long id, Long userId);

    Page<RatingEntity> findAllByUserId(Long userId, Pageable pageable);

    Optional<RatingEntity> findByUserIdAndSourceIdAndType(Long userId, Long sourceId, RatingType type);
}