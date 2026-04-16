package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.ClickTransactionEntity;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClickTransactionRepository extends AbstractRepository<ClickTransactionEntity> {

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ClickTransactionEntity save(ClickTransactionEntity clickTransactionEntity) {
        return getSession().merge(clickTransactionEntity);
    }

    @Override
    public Optional<ClickTransactionEntity> findById(Long id) {
        return getSession()
                .createQuery("from ClickTransactionEntity where id = :id", ClickTransactionEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<ClickTransactionEntity> findAll(int page, int size) {
        return getSession().createQuery("from ClickTransactionEntity ", ClickTransactionEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public boolean existsByMerchantTransId(String merchantTransId) {
        return getSession()
                .createQuery("select count(*) from ClickTransactionEntity where merchantTransId = :merchantTransId", Long.class)
                .setParameter("merchantTransId", merchantTransId)
                .uniqueResult() > 0;
    }

    public Optional<ClickTransactionEntity> findFirstByMerchantTransId(String merchantTransId) {
        return getSession()
                .createQuery("from ClickTransactionEntity where merchantTransId = :merchantTransId order by createdAt desc", ClickTransactionEntity.class)
                .setParameter("merchantTransId", merchantTransId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    public Optional<ClickTransactionEntity> findByIdAndMerchantTransId(Long id, String merchantTransId) {
        return getSession()
                .createQuery("from ClickTransactionEntity where id = :id and merchantTransId = :merchantTransId", ClickTransactionEntity.class)
                .setParameter("id", id)
                .setParameter("merchantTransId", merchantTransId)
                .uniqueResultOptional();
    }
}
