package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepository extends AbstractRepository<PaymentEntity>{

    private final SessionFactory sessionFactory;

    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }

    @Override
    public PaymentEntity save(PaymentEntity paymentEntity) {
        return getSession().merge(paymentEntity);
    }

    @Override
    public Optional<PaymentEntity> findById(Long id) {
        return getSession().createQuery("from PaymentEntity where id = :id", PaymentEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<PaymentEntity> findAll(int page, int size){
        return getSession().createQuery("from PaymentEntity ", PaymentEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<PaymentEntity> findByBookingIdAndUserIdOrderByCreatedAtDesc(Long bookingId, Long userId) {
        return getSession()
                .createQuery("from PaymentEntity where bookingId = :bookingId and userId = :userId order by createdAt desc ", PaymentEntity.class)
                .setParameter("bookingId", bookingId)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<PaymentEntity> findByUserIdAndTourIdAndBookingIdAndStatusIs(Long userId, Long tourId, Long bookingId, PaymentStatus paymentStatus) {
        return getSession()
                .createQuery("from PaymentEntity where userId = :userId and tourId = :tourId and bookingId = :bookingId and status = :status", PaymentEntity.class)
                .setParameter("userId", userId)
                .setParameter("tourId", tourId)
                .setParameter("bookingId", bookingId)
                .setParameter("status", paymentStatus)
                .uniqueResultOptional();
    }

    public List<PaymentEntity> findAllByUserId(Long userId, int page, int size) {
        return getSession()
                .createQuery("from PaymentEntity where userId = :userId", PaymentEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<PaymentEntity> findAllByTourId(Long tourId, int page, int size) {
        return getSession()
                .createQuery("from PaymentEntity where tourId = :tourId", PaymentEntity.class)
                .setParameter("tourId", tourId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<PaymentEntity> findAllByStatusIsOrderByCreatedAtDesc(PaymentStatus status, int page, int size) {
        return getSession()
                .createQuery("from PaymentEntity where status = :status order by createdAt desc ", PaymentEntity.class)
                .setParameter("status", status)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void saveAll(List<PaymentEntity> payments) {
        payments.forEach(payment -> getSession().merge(payment));
    }
}
