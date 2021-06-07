package org.zaproxy.zap.db.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordStructure;
import org.parosproxy.paros.db.TableStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.StructureModel;
import org.zaproxy.zap.db.repository.StructureModelRepository;

@Service
public class StructureDao implements TableStructure {

    @Autowired
    private StructureModelRepository structureRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordStructure read(long sessionId, long structureId) throws DatabaseException {
        return structureRepository.findFirstByIdAndSessionId(sessionId, structureId)
                .map(entity -> entity.toRecord())
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordStructure find(long sessionId, String name, String method) throws DatabaseException {
        return structureRepository.findFirstBySessionIdAndNameAndMethod(sessionId, name, method)
                .map(entity -> entity.toRecord())
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<RecordStructure> getChildren(long sessionId, long parentId) throws DatabaseException {
        return structureRepository.findBySessionIdAndParentId(sessionId, parentId)
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public long getChildCount(long sessionId, long parentId) throws DatabaseException {
        return structureRepository.countBySessionIdAndParentId(sessionId, parentId);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public RecordStructure insert(long sessionId, long parentId, int historyId, String name, String url, String method)
            throws DatabaseException {
        return structureRepository.save(StructureModel.builder()
                .sessionId(sessionId)
                .parentId(parentId)
                .historyId((long) historyId)
                .name(name)
                .url(url)
                .method(method)
                .build()).toRecord();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteLeaf(long sessionId, long structureId) throws DatabaseException {
        // TODO implement

    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteSubtree(long sessionId, long structureId) throws DatabaseException {
        // TODO implement

    }

}
