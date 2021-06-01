package org.zaproxy.zap.db.dao;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordAlert;
import org.parosproxy.paros.db.TableAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.AlertModel;
import org.zaproxy.zap.db.repository.AlertModelRepository;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class AlertDao implements TableAlert {

    @Autowired
    private AlertModelRepository alertRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    public RecordAlert read(int alertId) throws DatabaseException {
        return alertRepository.findById((long) alertId)
                .map(alert -> alert.toRecord())
                .orElseThrow(() -> new DatabaseException("Alert with id:" + alertId + " not found!"));
    }

    @Override
    public RecordAlert write(int scanId, int pluginId, String alert, int risk, int confidence, String description,
            String uri, String param, String attack, String otherInfo, String solution, String reference,
            String evidence, int cweId, int wascId, int historyId, int sourceHistoryId, int sourceId, String alertRef)
            throws DatabaseException {
        return alertRepository.save(AlertModel.builder()
                .scanId(scanId)
                .pluginId(pluginId)
                .name(alert)
                .risk(risk)
                .confidence(confidence)
                .description(description)
                .uri(uri)
                .param(param)
                .attack(attack)
                .otherInfo(otherInfo)
                .solution(solution)
                .reference(reference)
                .evidence(evidence)
                .cweId(cweId)
                .wascId(wascId)
                .historyId(historyId)
                .sourceHistoryId(sourceHistoryId)
                .sourceId(sourceId)
                .alertRef(alertRef)
                .build()).toRecord();
    }

    @Override
    public Vector<Integer> getAlertListBySession(long sessionId) throws DatabaseException {
        return alertRepository.findAllAlertIdBySessionId(sessionId)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toCollection(Vector::new));
    }

    @Override
    public void deleteAlert(int alertId) throws DatabaseException {
        alertRepository.deleteById((long) alertId);
    }

    @Override
    public int deleteAllAlerts() throws DatabaseException {
        long count = alertRepository.count();
        alertRepository.deleteAll();
        return (int) count;
    }

    @Override
    public void update(int alertId, String alert, int risk, int confidence, String description, String uri,
            String param, String attack, String otherInfo, String solution, String reference, String evidence,
            int cweId, int wascId, int sourceHistoryId) throws DatabaseException {
        AlertModel entity = alertRepository.findById((long) alertId)
                .orElseThrow(() -> new DatabaseException("Alert with id:" + alertId + " not found!"));
        entity.setName(alert);
        entity.setRisk(risk);
        entity.setConfidence(confidence);
        entity.setDescription(description);
        entity.setUri(uri);
        entity.setParam(param);
        entity.setAttack(attack);
        entity.setOtherInfo(otherInfo);
        entity.setSolution(solution);
        entity.setReference(reference);
        entity.setEvidence(evidence);
        entity.setCweId(cweId);
        entity.setWascId(wascId);
        entity.setSourceHistoryId(sourceHistoryId);
        alertRepository.save(entity);
    }

    @Override
    public void updateHistoryIds(int alertId, int historyId, int sourceHistoryId) throws DatabaseException {
        AlertModel entity = alertRepository.findById((long) alertId)
                .orElseThrow(() -> new DatabaseException("Alert with id:" + alertId + " not found!"));
        entity.setHistoryId(historyId);
        entity.setSourceHistoryId(sourceHistoryId);
        alertRepository.save(entity);
    }

    @Override
    public List<RecordAlert> getAlertsBySourceHistoryId(int historyId) throws DatabaseException {
        return alertRepository.findAllBySourceHistoryId(historyId)
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    public Vector<Integer> getAlertList() throws DatabaseException {
        return alertRepository.findAllAlertId()
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toCollection(Vector::new));
    }

}
