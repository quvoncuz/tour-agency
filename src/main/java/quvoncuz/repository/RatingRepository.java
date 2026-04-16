package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingRepository extends AbstractRepository<RatingEntity> {

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public RatingEntity save(RatingEntity entity) {
        return getSession().merge(entity);
    }

    @Override
    public Optional<RatingEntity> findById(Long id) {
        return getSession()
                .createQuery("from RatingEntity where id = :id", RatingEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<RatingEntity> findAll(int page, int size) {
        return getSession().createQuery("from RatingEntity ", RatingEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<RatingEntity> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size) {
        return getSession()
                .createQuery("from RatingEntity where sourceId = :sourceId and type = :type", RatingEntity.class)
                .setParameter("sourceId", sourceId)
                .setParameter("type", type)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public boolean deleteByIdAndUserId(Long id, Long userId) {
        Optional<RatingEntity> ratingEntity = getSession()
                .createQuery("from RatingEntity where id = :id and userId = :userId", RatingEntity.class)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .uniqueResultOptional();

        if (ratingEntity.isEmpty()) {
            return true;
        }
        getSession().remove(ratingEntity.get());
        return true;
    }

    public List<RatingEntity> findAllByUserId(Long userId, int page, int size) {
        return getSession()
                .createQuery("from RatingEntity where userId = :userId", RatingEntity.class)
                .setParameter("userId", userId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<RatingEntity> findAllBySourceIdAndType(Long sourceId, RatingType type) {
        return getSession()
                .createQuery("from RatingEntity where sourceId = :sourceId and type = :type", RatingEntity.class)
                .setParameter("sourceId", sourceId)
                .setParameter("type", type)
                .getResultList();
    }

    public Optional<RatingEntity> findByUserIdAndSourceIdAndType(Long userId, Long sourceId, RatingType type) {
        return getSession()
                .createQuery("from RatingEntity where userId = :userId and sourceId = :sourceId and type = :type", RatingEntity.class)
                .setParameter("userId", userId)
                .setParameter("sourceId", sourceId)
                .setParameter("type", type)
                .uniqueResultOptional();
    }
}