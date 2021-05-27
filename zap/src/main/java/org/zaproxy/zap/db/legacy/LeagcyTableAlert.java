package org.zaproxy.zap.db.legacy;

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
import org.zaproxy.zap.db.repository.AlertRepository;
import org.zaproxy.zap.db.schema.Alert;
import org.zaproxy.zap.db.schema.Alert.AlertBuilder;

@Service
public class LeagcyTableAlert implements TableAlert {

    @Autowired
    private AlertRepository alertRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // ignore
    }

    @Override
    public RecordAlert read(int alertId) throws DatabaseException {
        return alertRepository.findById((long) alertId)
                .map(alert -> alert.toRecord())
                .orElseThrow(() -> new DatabaseException("Alert with id:" + alertId + " not found!"));
    }

    @Override
    @Transactional
    public RecordAlert write(int scanId, int pluginId, String alert, int risk, int confidence, String description,
            String uri, String param, String attack, String otherInfo, String solution, String reference,
            String evidence, int cweId, int wascId, int historyId, int sourceHistoryId, int sourceId, String alertRef)
            throws DatabaseException {

        AlertBuilder builder = Alert.builder()
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
                .alertRef(alertRef);

        return alertRepository.save(builder.build()).toRecord();
    }

    @Override
    public Vector<Integer> getAlertListBySession(long sessionId) throws DatabaseException {
        return alertRepository.findAllAlertIdBySessionId(sessionId)
                .stream()
                .map(id -> id.intValue())
                .collect(Collectors.toCollection(Vector::new));
    }

    @Override
    @Transactional
    public void deleteAlert(int alertId) throws DatabaseException {
        alertRepository.deleteById((long) alertId);
    }

    @Override
    @Transactional
    public int deleteAllAlerts() throws DatabaseException {
        long count = alertRepository.count();
        alertRepository.deleteAll();
        return (int) count;
    }

    @Override
    @Transactional
    public void update(int alertId, String alert, int risk, int confidence, String description, String uri,
            String param, String attack, String otherInfo, String solution, String reference, String evidence,
            int cweId, int wascId, int sourceHistoryId) throws DatabaseException {
        Alert entity = alertRepository.findById((long) alertId)
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
    @Transactional
    public void updateHistoryIds(int alertId, int historyId, int sourceHistoryId) throws DatabaseException {
        Alert entity = alertRepository.findById((long) alertId)
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
