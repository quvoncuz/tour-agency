package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.TourEntity;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TourRepository extends AbstractRepository<TourEntity> {

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public TourEntity save(TourEntity entity) {
        return getSession().merge(entity);
    }

    @Override
    public Optional<TourEntity> findById(Long id) {
        return getSession()
                .createQuery("from TourEntity where id = :id", TourEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<TourEntity> findAll(int page, int size) {
        return getSession().createQuery("from TourEntity ", TourEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<TourEntity> findAllByAgencyId(Long agencyId) {
        return getSession()
                .createQuery("from TourEntity where agencyId = :agencyId", TourEntity.class)
                .setParameter("agencyId", agencyId)
                .getResultList();
    }

    public void updateRating(double averageRating, Long sourceId) {
        TourEntity tour = findById(sourceId).orElseThrow(() -> new NotFoundException("Agency not found"));
        tour.setRating(averageRating);
        getSession().merge(tour);
    }

    public List<TourEntity> findAllByIdIn(List<Long> ids, int page, int size) {
        return getSession()
                .createQuery("from TourEntity where id in :ids", TourEntity.class)
                .setParameter("ids", ids)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void deleteByIdAndAgencyId(Long id, Long agencyId) {
        TourEntity tour = findById(id).orElseThrow(() -> new NotFoundException("Tour not found"));
        if (!tour.getAgencyId().equals(agencyId)) {
            throw new DoNotMatchException("Only owner can delete tour");
        }
        getSession().remove(tour);
    }
    //    @Query("select t from TourEntity as t where lower(t.title) like ?1 or lower(t.description) like ?1 or lower(t.destination) like ?1 order by t.createdAt desc ")

    public List<TourEntity> findAllByQuery(String query, int page, int size) {
        return getSession()
                .createQuery("from TourEntity where lower(title) like :query or lower(description) like :query or lower(destination) like :query order by createdAt desc ", TourEntity.class)
                .setParameter("query", "%" + query + "%")
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
