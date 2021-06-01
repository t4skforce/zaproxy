package org.zaproxy.zap.db.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.zaproxy.zap.db.model.TagModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface TagModelRepository extends CacheableCrudRepository<TagModel, Long> {

    void deleteByHistoryIdAndTag(Long historyId, String tag);

    void deleteByHistoryId(Long historyId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    List<TagModel> findAllByHistoryId(Long historyId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT DISTINCT tag FROM #{#entityName} ORDER BY tag")
    List<String> findAllTags();

}
