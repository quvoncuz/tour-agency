package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.SavedTourEntity;

@Repository
public interface SavedTourRepository extends JpaRepository<SavedTourEntity, Long> {
}
