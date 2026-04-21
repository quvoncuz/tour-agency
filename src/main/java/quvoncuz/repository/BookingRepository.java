package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepository extends AbstractRepository<BookingEntity> {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public BookingEntity save(BookingEntity bookingEntity) {
        return getSession().merge(bookingEntity);
    }

    @Override
    public Optional<BookingEntity> findById(Long id) {
        return getSession()
                .createQuery("from BookingEntity where id = :id", BookingEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<BookingEntity> findAll(int page, int size) {
        return getSession().createQuery("from BookingEntity ", BookingEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public boolean existsByTourIdAndUserId(Long tourId, Long userId) {
        return getSession().createQuery("select count(*) from BookingEntity where tourId = :tourId and userId = :userId", Long.class)
                .setParameter("tourId", tourId)
                .setParameter("userId", userId)
                .uniqueResult() > 0;

    }

    public List<BookingEntity> findAllByTourId(Long tourId, int page, int size) {
        return getSession()
                .createQuery("from BookingEntity where tourId = :tourId", BookingEntity.class)
                .setParameter("tourId", tourId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<BookingEntity> findAllByTourIdIsIn(List<Long> tourIds, int page, int size) {
        return getSession()
                .createQuery("from BookingEntity where tourId in : tourIds", BookingEntity.class)
                .setParameter("tourIds", tourIds)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void updateStatus(BookingStatus bookingStatus, Long bookingId) {
        getSession()
                .createQuery("update BookingEntity set status = :status where id = :id", BookingEntity.class)
                .setParameter("status", bookingStatus)
                .setParameter("id", bookingId)
                .executeUpdate();
    }

    public List<BookingEntity> findAllByUserId(Long userId) {
        return getSession()
                .createQuery("from BookingEntity where userId = :userId", BookingEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public boolean existsByTourIdAndUserIdAndStatusIs(Long tourId, Long userId, BookingStatus status) {
        return getSession()
                .createQuery("select count(*) from BookingEntity where tourId = :tourId and userId = :userId and status = :status", Long.class)
                .setParameter("tourId", tourId)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .uniqueResult() > 0;
    }

    public boolean existsByTourIdAndUserIdAndStatusIsNot(Long tourId, Long userId, BookingStatus status) {
        return getSession()
                .createQuery("select count(*) from BookingEntity where tourId = :tourId and userId = :userId and status <> :status", Long.class)
                .setParameter("tourId", tourId)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .uniqueResult() > 0;
    }

    public Optional<BookingEntity> findByIdAndUserId(long bookingId, long userId) {
        return getSession()
                .createQuery("from BookingEntity where id = :id and userId = :userId",  BookingEntity.class)
                .setParameter("id", bookingId)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }

    public List<BookingEntity> findAllByTourIdAndStatus(Long tourId, BookingStatus bookingStatus) {
        return getSession()
                .createQuery("from BookingEntity where tourId = :tourId and status = :status", BookingEntity.class)
                .setParameter("tourId", tourId)
                .setParameter("status", bookingStatus)
                .getResultList();
    }

    public void saveAll(List<BookingEntity> bookings) {
        bookings.forEach(booking -> getSession().merge(booking));
    }

    public List<BookingEntity> findAllUpdated(long userId) {
        return getSession().createQuery("from BookingEntity where userId = :userId and status = :status", BookingEntity.class)
                .setParameter("userId", userId)
                .setParameter("status", BookingStatus.ON_UPDATE)
                .getResultList();
    }
}
