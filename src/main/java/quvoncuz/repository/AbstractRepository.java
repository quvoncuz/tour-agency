package quvoncuz.repository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T> {

    public abstract T save(T t);

    public abstract Optional<T> findById(Long id);

    public abstract List<T> findAll(int page, int size);
}
