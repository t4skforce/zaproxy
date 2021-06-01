package org.zaproxy.zap.db.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.zaproxy.zap.db.model.SessionUrlModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface SessionUrlModelRepository extends CacheableCrudRepository<SessionUrlModel, Long> {

    void deleteByTypeAndUrl(Integer type, String url);

    void deleteByType(Integer type);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    List<SessionUrlModel> findAllByType(Integer type);

}
