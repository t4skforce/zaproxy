package org.zaproxy.zap.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.zaproxy.zap.db.schema.WebSocketChannel;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface WebSocketChannelRepository extends CrudRepository<WebSocketChannel, Long> {

}
