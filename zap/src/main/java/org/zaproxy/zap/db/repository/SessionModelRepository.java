package org.zaproxy.zap.db.repository;

import org.zaproxy.zap.db.model.SessionModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface SessionModelRepository extends CacheableCrudRepository<SessionModel, Long> {

}
