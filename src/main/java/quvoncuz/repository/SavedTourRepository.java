package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SavedTourRepository extends AbstractRepository<SavedTourEntity> {

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public SavedTourEntity save(SavedTourEntity savedTourEntity) {
        return getSession().merge(savedTourEntity);
    }

    @Override
    public Optional<SavedTourEntity> findById(Long id) {
        return getSession()
                .createQuery("from SavedTourEntity where id = :id", SavedTourEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<SavedTourEntity> findAll(int page, int size) {
        return getSession().createQuery("from SavedTourEntity ", SavedTourEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public boolean existsByTourIdAndUserId(Long tourId, Long userId) {
        return getSession()
                .createQuery("select count(*) from SavedTourEntity where tourId = :tourId and userId = :userId", Long.class)
                .setParameter("tourId", tourId)
                .setParameter("userId", userId)
                .uniqueResult() > 0;
    }

    public Boolean deleteByTourIdAndUserId(Long tourId, Long userId) {
        SavedTourEntity savedTour = getSession()
                .createQuery("select count(*) from SavedTourEntity where tourId = :tourId and userId = :userId", SavedTourEntity.class)
                .setParameter("tourId", tourId)
                .setParameter("userId", userId)
                .uniqueResultOptional()
                .orElseThrow(() -> new NotFoundException("Not found exception"));
        getSession().remove(savedTour);
        return true;
    }

    public List<SavedTourEntity> findAllByUserId(Long userId) {
        return getSession()
                .createQuery("from SavedTourEntity where userId = :userId", SavedTourEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
