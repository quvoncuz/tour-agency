package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AgencyRepository extends AbstractRepository<AgencyEntity>{

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    public Optional<AgencyEntity> findByOwnerId(Long ownerId) {
        return getSession()
                .createQuery("from AgencyEntity where ownerId = :ownerId", AgencyEntity.class)
                .setParameter("ownerId", ownerId)
                .uniqueResultOptional();
    }

    @Override
    public AgencyEntity save(AgencyEntity agencyEntity) {
        return getSession().merge(agencyEntity);
    }


    @Override
    public Optional<AgencyEntity> findById(Long agencyId) {
        return getSession()
                .createQuery("from AgencyEntity where id = :id", AgencyEntity.class)
                .setParameter("id", agencyId)
                .uniqueResultOptional();
    }

    @Override
    public List<AgencyEntity> findAll(int page, int size){
        return getSession().createQuery("from AgencyEntity ", AgencyEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void deleteById(Long agencyId) {
        AgencyEntity agency = findById(agencyId).orElseThrow(() -> new NotFoundException("Agency not found"));
        getSession().remove(agency);
    }

    public void updateRating(double averageRating, Long agencyId) {
        AgencyEntity agency = findById(agencyId).orElseThrow(() -> new NotFoundException("Agency not found"));
        agency.setRating(averageRating);
        getSession().merge(agency);
    }
}
