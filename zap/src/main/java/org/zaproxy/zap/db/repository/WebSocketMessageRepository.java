package org.zaproxy.zap.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.zaproxy.zap.db.schema.WebSocketMessage;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface WebSocketMessageRepository extends CrudRepository<WebSocketMessage, Long> {

}
