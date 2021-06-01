package org.zaproxy.zap.db.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.zaproxy.zap.db.model.StructureModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface StructureModelRepository extends CacheableCrudRepository<StructureModel, Long> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    Optional<StructureModel> findFirstByIdAndSessionId(Long sessionId, Long structureId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    Optional<StructureModel> findFirstBySessionIdAndNameAndMethod(Long sessionId, String name, String method);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    List<StructureModel> findBySessionIdAndParentId(Long sessionId, Long parentId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    Long countBySessionIdAndParentId(Long sessionId, Long parentId);
}
