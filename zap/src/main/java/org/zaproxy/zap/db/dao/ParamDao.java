package org.zaproxy.zap.db.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordParam;
import org.parosproxy.paros.db.TableParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.ParamModel;
import org.zaproxy.zap.db.repository.ParamModelRepository;

@Service
public class ParamDao implements TableParam {

    @Autowired
    private ParamModelRepository paramRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordParam read(long urlId) throws DatabaseException {
        return paramRepository.findById(urlId)
                .map(entity -> entity.toRecord())
                .orElseThrow(() -> new DatabaseException("Param with id:" + urlId + " doesn't exist!"));
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<RecordParam> getAll() throws DatabaseException {
        return IterableUtils.toList(paramRepository.findAll())
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public RecordParam insert(String site, String type, String name, int used, String flags, String values)
            throws DatabaseException {
        return paramRepository.save(
                ParamModel.builder().site(site).type(type).name(name).used(used).flags(flags).values(values).build())
                .toRecord();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void update(long paramId, int used, String flags, String values) throws DatabaseException {
        paramRepository.findById(paramId).ifPresent(entity -> {
            entity.setUsed(used);
            entity.setFlags(flags);
            entity.setValues(values);
            paramRepository.save(entity);
        });
    }

}
