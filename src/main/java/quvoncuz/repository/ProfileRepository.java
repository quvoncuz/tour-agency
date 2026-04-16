package quvoncuz.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileRepository extends AbstractRepository<ProfileEntity> {

    private final SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ProfileEntity save(ProfileEntity profile) {
        return getSession().merge(profile);
    }

    @Override
    public Optional<ProfileEntity> findById(Long id) {
        return getSession()
                .createQuery("from ProfileEntity where id = :id", ProfileEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public List<ProfileEntity> findAll(int page, int size) {
        return getSession().createQuery("from ProfileEntity ", ProfileEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public Optional<ProfileEntity> findByUsername(String username) {
        return getSession()
                .createQuery("from ProfileEntity where username = :username", ProfileEntity.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public boolean existsByUsername(String username) {
        return getSession()
                .createQuery("select count(*) from ProfileEntity where username = :username", Long.class)
                .setParameter("username", username)
                .uniqueResult() > 0;
    }

    public boolean existsByEmail(String email) {
        return getSession()
                .createQuery("select count(*) from ProfileEntity where email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult() > 0;
    }

    public void deleteById(Long id) {
        ProfileEntity profile = findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        getSession().remove(profile);
    }

    public void updateProfileRole(Role role, long userId) {
        getSession()
                .createQuery("update ProfileEntity set role = :role where id = :userId")
                .setParameter("role", role)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
