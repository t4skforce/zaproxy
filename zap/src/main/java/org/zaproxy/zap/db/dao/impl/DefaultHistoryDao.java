package org.zaproxy.zap.db.dao.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordHistory;
import org.parosproxy.paros.db.TableHistory;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.dao.HistoryDao;
import org.zaproxy.zap.db.model.HistoryModel;
import org.zaproxy.zap.db.model.HistoryModel.HistoryModelBuilder;
import org.zaproxy.zap.db.repository.HistoryModelRepository;
import org.zaproxy.zap.network.HttpRequestBody;
import org.zaproxy.zap.network.HttpResponseBody;

@Service
public class DefaultHistoryDao implements TableHistory, HistoryDao {

    @Autowired
    private HistoryModelRepository historyRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordHistory read(int historyId) throws HttpMalformedHeaderException, DatabaseException {
        return historyRepository.findById((long) historyId)
                .orElseThrow(() -> new DatabaseException("History entry with id" + historyId + " not found!"))
                .toRecord();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public RecordHistory write(long sessionId, int histType, HttpMessage msg)
            throws HttpMalformedHeaderException, DatabaseException {
        HistoryModelBuilder builder = HistoryModel.builder();

        HttpRequestHeader reqHeader = msg.getRequestHeader();
        if (!reqHeader.isEmpty()) {
            HttpRequestBody reqBody = msg.getRequestBody();
            builder.requestHeader(reqHeader.toString())
                    .requestBody(reqBody.getBytes())
                    .method(reqHeader.getMethod())
                    .uri(reqHeader.getURI().toString());
        }

        HttpResponseHeader respHeader = msg.getResponseHeader();
        if (!respHeader.isEmpty()) {
            HttpResponseBody respBody = msg.getResponseBody();
            builder.responseHeader(respHeader.toString())
                    .responseBody(respBody.getBytes())
                    .statusCode(respHeader.getStatusCode());
        }

        builder.sessionId(sessionId)
                .type(histType)
                .timeSentMillis(msg.getTimeSentMillis())
                .timeElapsedMillis((long) msg.getTimeElapsedMillis())
                .note(msg.getNote())
                .responseFromTargetHost(msg.isResponseFromTargetHost());

        return historyRepository.save(builder.build()).toRecord();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIds(long sessionId) throws DatabaseException {
        return historyRepository.findIdsBySessionId(sessionId)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIdsStartingAt(long sessionId, int startAtHistoryId) throws DatabaseException {
        return historyRepository.findIdsBySessionIdStartingAt(sessionId, (long) startAtHistoryId)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIdsOfHistType(long sessionId, int... histTypes) throws DatabaseException {
        return historyRepository.findIdsBySessionIdAndType(sessionId, histTypes)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIdsOfHistTypeStartingAt(long sessionId, int startAtHistoryId, int... histTypes)
            throws DatabaseException {
        return historyRepository.findIdsBySessionIdAndTypeStartingAt(sessionId, (long) startAtHistoryId, histTypes)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIdsExceptOfHistType(long sessionId, int... histTypes) throws DatabaseException {
        return historyRepository.findIdsBySessionIdAndNotType(sessionId, histTypes)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryIdsExceptOfHistTypeStartingAt(long sessionId, int startAtHistoryId, int... histTypes)
            throws DatabaseException {
        return historyRepository.findIdsBySessionIdAndNotTypeStartingAt(sessionId, (long) startAtHistoryId, histTypes)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toList());
    }

    // TODO: move to DB Query and regex fallback via UI/flag
    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<Integer> getHistoryList(long sessionId, int histType, String filter, boolean isRequest)
            throws DatabaseException {
        Pattern pattern = Pattern.compile(filter, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        return historyRepository.findBySessionIdAndTypeOrderByIdAsc(sessionId, histType)
                .parallelStream()
                .filter(history -> {
                    if (isRequest) {
                        return pattern.matcher(history.getRequestHeader()).find()
                                || pattern.matcher(new String(history.getRequestBody(), StandardCharsets.UTF_8)).find();
                    }
                    return pattern.matcher(history.getResponseHeader()).find()
                            || pattern.matcher(new String(history.getResponseBody(), StandardCharsets.UTF_8)).find();
                })
                .map(history -> history.getId().intValue())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteHistorySession(long sessionId) throws DatabaseException {
        historyRepository.deleteBySessionId(sessionId);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteHistoryType(long sessionId, int historyType) throws DatabaseException {
        historyRepository.deleteBySessionIdAndType(sessionId, historyType);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void delete(int historyId) throws DatabaseException {
        historyRepository.deleteById((long) historyId);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void delete(List<Integer> ids) throws DatabaseException {
        if (CollectionUtils.isNotEmpty(ids)) {
            historyRepository.deleteAllById(ids.stream().map(id -> (long) id).collect(Collectors.toList()));
        }
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void delete(List<Integer> ids, int batchSize) throws DatabaseException {
        if (!CollectionUtils.isNotEmpty(ids)) {
            ListUtils.partition(ids.stream().map(id -> (long) id).collect(Collectors.toList()), batchSize)
                    .parallelStream()
                    .forEach(idl -> historyRepository.deleteAllById(idl));
        }
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteTemporary() throws DatabaseException {
        historyRepository.deleteAllByType(HistoryReference.getTemporaryTypes());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public boolean containsURI(long sessionId, int historyType, String method, String uri, byte[] body)
            throws DatabaseException {
        return historyRepository
                .findFirstByUriAndMethodAndRequestBodyAndSessionIdAndType(uri, method,
                        new String(body, StandardCharsets.UTF_8), sessionId, historyType)
                .isPresent();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordHistory getHistoryCache(HistoryReference ref, HttpMessage reqMsg)
            throws DatabaseException, HttpMalformedHeaderException {
        HttpRequestHeader reqHeader = reqMsg.getRequestHeader();

        List<HistoryModel> history = historyRepository.findAllHistoryCache((long) ref.getHistoryId(),
                reqHeader.getURI().toString(), reqHeader.getMethod(), reqMsg.getRequestBody().toString(),
                ref.getSessionId(), PageRequest.of(0, 1));
        if (CollectionUtils.isNotEmpty(history)) {
            return history.get(0).toRecord();
        }

        history = historyRepository.findAllHistoryCacheBefore(reqHeader.getURI().toString(), reqHeader.getMethod(),
                reqMsg.getRequestBody().toString(), ref.getSessionId(), PageRequest.of(0, 1));
        if (CollectionUtils.isNotEmpty(history)) {
            return history.get(0).toRecord();
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void updateNote(int historyId, String note) throws DatabaseException {
        historyRepository.findById((long) historyId).ifPresent(history -> {
            history.setNote(note);
            historyRepository.save(history);
        });
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public int lastIndex() {
        return historyRepository.findMaxId().orElse(0L).intValue();
    }

}
