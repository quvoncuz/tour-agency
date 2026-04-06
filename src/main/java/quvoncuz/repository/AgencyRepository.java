package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.entities.AgencyEntity;

import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<AgencyEntity, Long> {

    @Modifying
    @Transactional
    @Query("update AgencyEntity set rating = ?1 where id = ?2")
    void updateRating(double averageRating, Long agencyId);

    Optional<AgencyEntity> findByOwnerId(Long ownerId);
}
