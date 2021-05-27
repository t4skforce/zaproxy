package org.zaproxy.zap.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zaproxy.zap.db.schema.Alert;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface AlertRepository extends CrudRepository<Alert, Long> {

    @Query("SELECT a.id FROM #{#entityName} a JOIN Scan s ON a.scanId = s.id AND s.id = :sessionId ORDER BY id")
    List<Long> findAllAlertIdBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT id FROM #{#entityName} ORDER BY id")
    List<Long> findAllAlertId();

    List<Alert> findAllBySourceHistoryId(int historyId);

}
