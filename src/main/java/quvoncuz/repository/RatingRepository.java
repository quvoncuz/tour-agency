package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.RatingEntity;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

}