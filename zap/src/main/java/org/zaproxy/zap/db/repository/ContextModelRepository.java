package org.zaproxy.zap.db.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.zaproxy.zap.db.model.ContextModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface ContextModelRepository extends CacheableCrudRepository<ContextModel, Long> {

    void deleteByContextIdAndTypeAndData(Long contextId, Integer type, String data);

    void deleteByContextIdAndType(Long contextId, Integer type);

    void deleteByContextId(Long contextId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    List<ContextModel> findAllByContextId(Long contextId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    List<ContextModel> findAllByContextIdAndType(Long contextId, Integer type);

}
