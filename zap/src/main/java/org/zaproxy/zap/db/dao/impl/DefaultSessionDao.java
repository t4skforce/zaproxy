package org.zaproxy.zap.db.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordSession;
import org.parosproxy.paros.db.TableSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.dao.SessionDao;
import org.zaproxy.zap.db.model.SessionModel;
import org.zaproxy.zap.db.repository.SessionModelRepository;

@Service
public class DefaultSessionDao implements TableSession, SessionDao {

    @Autowired
    private SessionModelRepository sessionRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void insert(long sessionId, String sessionName) throws DatabaseException {
        SessionModel entity = SessionModel.builder().id(sessionId).name(sessionName).build();
        sessionRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void update(long sessionId, String sessionName) throws DatabaseException {
        SessionModel entity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DatabaseException("Session with id:" + sessionId + " not found!"));
        entity.setName(sessionName);
        sessionRepository.save(entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<RecordSession> listSessions() throws DatabaseException {
        return IterableUtils.toList(sessionRepository.findAll())
                .stream()
                .map(s -> s.toRecord())
                .collect(Collectors.toList());
    }

}
