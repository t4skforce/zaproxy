package org.zaproxy.zap.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.zaproxy.zap.db.schema.Structure;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface StructureRepository extends CrudRepository<Structure, Long> {

}
