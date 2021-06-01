package org.zaproxy.zap.db.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordContext;
import org.parosproxy.paros.db.TableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.ContextModel;
import org.zaproxy.zap.db.repository.ContextModelRepository;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class ContextDao implements TableContext {

    @Autowired
    private ContextModelRepository contextDataRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    public RecordContext read(long dataId) throws DatabaseException {
        return contextDataRepository.findById(dataId)
                .map(entity -> entity.toRecord())
                .orElseThrow(() -> new DatabaseException("Context with id:" + dataId + " not found!"));
    }

    @Override
    public RecordContext insert(int contextId, int type, String url) throws DatabaseException {
        return contextDataRepository.save(ContextModel.builder().contextId(contextId).type(type).data(url).build())
                .toRecord();
    }

    @Override
    public void delete(int contextId, int type, String data) throws DatabaseException {
        contextDataRepository.deleteByContextIdAndTypeAndData(contextId, type, data);
    }

    @Override
    public void deleteAllDataForContextAndType(int contextId, int type) throws DatabaseException {
        contextDataRepository.deleteByContextIdAndType(contextId, type);
    }

    @Override
    public void deleteAllDataForContext(int contextId) throws DatabaseException {
        contextDataRepository.deleteByContextId(contextId);
    }

    @Override
    public List<RecordContext> getAllData() throws DatabaseException {
        return IterableUtils.toList(contextDataRepository.findAll())
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordContext> getDataForContext(int contextId) throws DatabaseException {
        return IterableUtils.toList(contextDataRepository.findAllByContextId(contextId))
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordContext> getDataForContextAndType(int contextId, int type) throws DatabaseException {
        return IterableUtils.toList(contextDataRepository.findAllByContextIdAndType(contextId, type))
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    public void setData(int contextId, int type, List<String> dataList) throws DatabaseException {
        contextDataRepository.deleteByContextIdAndType(contextId, type);
        if (CollectionUtils.isNotEmpty(dataList)) {
            contextDataRepository.saveAll(dataList.stream()
                    .map(data -> ContextModel.builder().contextId(contextId).type(type).data(data).build())
                    .collect(Collectors.toList()));
        }
    }

}
