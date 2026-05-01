package quvoncuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.ClickTransactionEntity;

import java.util.Optional;

@Repository
public interface ClickTransactionRepository extends JpaRepository<ClickTransactionEntity, Long> {

    boolean existsByMerchantTransId(String merchantTransId);

    Optional<ClickTransactionEntity> findFirstByMerchantTransIdOrderByCreatedAtDesc(String merchantTransId);

    Optional<ClickTransactionEntity> findByIdAndMerchantTransId(Long id, String merchantTransId);
}
