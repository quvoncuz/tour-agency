package quvoncuz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    Page<RatingEntity> findBySourceIdAndType(Long sourceId, RatingType type, Pageable pageable);

    boolean deleteByIdAndUserId(Long id, Long userId);

    Page<RatingEntity> findAllByUserId(Long userId, Pageable pageable);

    List<RatingEntity> findAllBySourceIdAndType(Long sourceId, RatingType type);

    Optional<RatingEntity> findByUserIdAndSourceIdAndType(Long userId, Long sourceId, RatingType type);
}