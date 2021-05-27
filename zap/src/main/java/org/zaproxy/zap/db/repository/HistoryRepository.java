package org.zaproxy.zap.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zaproxy.zap.db.schema.History;

/**
 * https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface HistoryRepository extends CrudRepository<History, Long> {

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId ORDER BY id")
    List<Long> findIdsBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId ORDER BY id")
    List<Long> findIdsBySessionIdStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId);

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND type IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histTypes);

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId AND type IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndTypeStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId, @Param("histTypes") int... histTypes);

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND type NOT IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndNotType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histTypes);

    @Query("SELECT id FROM #{#entityName} WHERE sessionId = :sessionId AND id >= :startAtHistoryId AND type NOT IN (:histTypes) ORDER BY id")
    List<Long> findIdsBySessionIdAndNotTypeStartingAt(@Param("sessionId") Long sessionId,
            @Param("startAtHistoryId") Long startAtHistoryId, @Param("histTypes") int... histTypes);

    List<History> findBySessionIdAndTypeOrderByIdAsc(Long sessionId, int histType);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE sessionId = :sessionId AND type IN (:histTypes)")
    void deleteBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("histTypes") int... histType);

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE type IN (:histTypes)")
    void deleteAllByType(@Param("histTypes") Iterable<Integer> types);

    Optional<History> findFirstByUriAndMethodAndRequestBodyAndSessionIdAndType(String uri, String method,
            String requestBody, Long sessionId, int type);

    @Query("SELECT h FROM #{#entityName} h WHERE uri = :uri AND method = :method AND requestBody = :requestBody AND sessionId = :sessionId AND statusCode != #{T(org.parosproxy.paros.network.HttpStatusCode).NOT_MODIFIED} AND id >= :id AND id <= (:id + 200)")
    List<History> findAllHistoryCache(@Param("id") Long id, @Param("uri") String uri, @Param("method") String method,
            @Param("requestBody") String requestBody, @Param("sessionId") Long sessionId, Pageable page);

    @Query("SELECT h FROM #{#entityName} h WHERE uri = :uri AND method = :method AND requestBody = :requestBody AND sessionId = :sessionId AND statusCode != #{T(org.parosproxy.paros.network.HttpStatusCode).NOT_MODIFIED} ")
    List<History> findAllHistoryCacheBefore(@Param("uri") String uri, @Param("method") String method,
            @Param("requestBody") String requestBody, @Param("sessionId") Long sessionId, Pageable page);

    Optional<History> findTopByOrderByIdDesc();
}
