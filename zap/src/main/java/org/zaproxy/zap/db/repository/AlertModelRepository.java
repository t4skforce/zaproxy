package org.zaproxy.zap.db.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.zaproxy.zap.db.model.AlertModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface AlertModelRepository extends CacheableCrudRepository<AlertModel, Long> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT alert.id FROM #{#entityName} alert WHERE alert.scanId.id = :sessionId ORDER BY id")
    List<Long> findAllAlertIdBySessionId(@Param("sessionId") Long sessionId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} ORDER BY id")
    List<Long> findAllAlertId();

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT alert FROM #{#entityName} alert WHERE alert.historyId = :historyId")
    List<AlertModel> findAllBySourceHistoryId(@Param("historyId") Long historyId);

}
