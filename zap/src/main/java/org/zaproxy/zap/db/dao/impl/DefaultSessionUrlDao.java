package org.zaproxy.zap.db.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordSessionUrl;
import org.parosproxy.paros.db.TableSessionUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.dao.SessionUrlDao;
import org.zaproxy.zap.db.model.SessionUrlModel;
import org.zaproxy.zap.db.repository.SessionUrlModelRepository;

@Service
public class DefaultSessionUrlDao implements TableSessionUrl, SessionUrlDao {

    @Autowired
    private SessionUrlModelRepository sessionUrlRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public RecordSessionUrl read(long urlId) throws DatabaseException {
        return sessionUrlRepository.findById(urlId)
                .map(entity -> entity.toRecord())
                .orElseThrow(() -> new DatabaseException("SessionUrl with id:" + urlId + " not found!"));
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public RecordSessionUrl insert(int type, String url) throws DatabaseException {
        return sessionUrlRepository.save(SessionUrlModel.builder().type(type).url(url).build()).toRecord();
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void delete(int type, String url) throws DatabaseException {
        sessionUrlRepository.deleteByTypeAndUrl(type, url);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void deleteAllUrlsForType(int type) throws DatabaseException {
        sessionUrlRepository.deleteByType(type);
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class }, readOnly = true)
    public List<RecordSessionUrl> getUrlsForType(int type) throws DatabaseException {
        return sessionUrlRepository.findAllByType(type)
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = { DatabaseException.class })
    public void setUrls(int type, List<String> urls) throws DatabaseException {
        sessionUrlRepository.deleteByType(type);
        if (CollectionUtils.isNotEmpty(urls)) {
            sessionUrlRepository.saveAll(urls.stream()
                    .map(url -> SessionUrlModel.builder().type(type).url(url).build())
                    .collect(Collectors.toList()));
        }
    }

}
