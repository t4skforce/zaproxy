package org.zaproxy.zap.db.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.zaproxy.zap.db.model.HistoryModel;
import org.zaproxy.zap.db.repository.base.CacheableCrudRepository;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface HistoryModelRepository extends CacheableCrudRepository<HistoryModel, Long> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId ORDER BY id")
    List<Long> findIdsBySessionId(@Param("sessionId") Long sessionId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId ORDER BY id")
    List<Long> findIdsBySessionIdStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND type IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histTypes);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId AND type IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndTypeStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId, @Param("histTypes") int... histTypes);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND type NOT IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndNotType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histTypes);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId AND type NOT IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndNotTypeStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId, @Param("histTypes") int... histTypes);

    List<HistoryModel> findBySessionIdAndTypeOrderByIdAsc(Long sessionId, int histType);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE sessionId = :sessionId AND type IN (:histTypes)")
    void deleteBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histType);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE type IN (:histTypes)")
    void deleteAllByType(@Param("histTypes") Iterable<Integer> types);

    Optional<HistoryModel> findFirstByUriAndMethodAndRequestBodyAndSessionIdAndType(String uri, String method,
            String requestBody, Long sessionId, int type);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT h FROM #{#entityName} h WHERE uri = :uri AND method = :method AND requestBody = :requestBody AND sessionId = :sessionId AND statusCode != #{T(org.parosproxy.paros.network.HttpStatusCode).NOT_MODIFIED} AND id >= :id AND id <= (:id + 200)")
    List<HistoryModel> findAllHistoryCache(@Param("id") Long id, @Param("uri") String uri,
            @Param("method") String method, @Param("requestBody") String requestBody,
            @Param("sessionId") Long sessionId, Pageable page);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT h FROM #{#entityName} h WHERE uri = :uri AND method = :method AND requestBody = :requestBody AND sessionId = :sessionId AND statusCode != #{T(org.parosproxy.paros.network.HttpStatusCode).NOT_MODIFIED} ")
    List<HistoryModel> findAllHistoryCacheBefore(@Param("uri") String uri, @Param("method") String method,
            @Param("requestBody") String requestBody, @Param("sessionId") Long sessionId, Pageable page);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("SELECT MAX(id) FROM #{#entityName}")
    Optional<Long> findMaxId();
}
