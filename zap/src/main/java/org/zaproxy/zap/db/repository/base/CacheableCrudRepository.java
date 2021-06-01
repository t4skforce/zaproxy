package org.zaproxy.zap.db.repository.base;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CacheableCrudRepository<T, ID> extends CrudRepository<T, ID> {

    @Override
    @QueryHints(value = { @QueryHint(name = HINT_CACHEABLE, value = "true") })
    Optional<T> findById(ID id);

    @Override
    @QueryHints(value = { @QueryHint(name = HINT_CACHEABLE, value = "true") })
    boolean existsById(ID id);

    @Override
    @QueryHints(value = { @QueryHint(name = HINT_CACHEABLE, value = "true") })
    Iterable<T> findAll();

    @Override
    @QueryHints(value = { @QueryHint(name = HINT_CACHEABLE, value = "true") })
    Iterable<T> findAllById(Iterable<ID> ids);

    @Override
    @QueryHints(value = { @QueryHint(name = HINT_CACHEABLE, value = "true") })
    long count();

}
