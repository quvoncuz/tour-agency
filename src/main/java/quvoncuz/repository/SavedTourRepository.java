package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.SavedTourEntity;

import java.util.List;

@Repository
public interface SavedTourRepository extends JpaRepository<SavedTourEntity, Long> {

    boolean existsByTourIdAndUserId(Long tourId, Long userId);

    Boolean deleteByTourIdAndUserId(Long tourId, Long userId);

    List<SavedTourEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
