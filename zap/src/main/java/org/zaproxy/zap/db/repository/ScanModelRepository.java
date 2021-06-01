package org.zaproxy.zap.db.repository;

import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.zaproxy.zap.db.model.ScanModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface ScanModelRepository extends CacheableCrudRepository<ScanModel, Long> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    Optional<ScanModel> findTopByOrderByIdDesc();

}
