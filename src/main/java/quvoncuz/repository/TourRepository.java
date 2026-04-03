package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.TourEntity;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {

}
