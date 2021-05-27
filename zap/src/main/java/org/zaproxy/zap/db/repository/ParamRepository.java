package org.zaproxy.zap.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.zaproxy.zap.db.schema.Param;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface ParamRepository extends CrudRepository<Param, Long> {

}
