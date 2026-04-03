package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.AgencyEntity;

@Repository
public interface AgencyRepository extends JpaRepository<AgencyEntity, Long> {
}
