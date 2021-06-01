package org.zaproxy.zap.db.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordTag;
import org.parosproxy.paros.db.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.TagModel;
import org.zaproxy.zap.db.repository.HistoryModelRepository;
import org.zaproxy.zap.db.repository.TagModelRepository;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class TagDao implements TableTag {

    @Autowired
    private TagModelRepository tagRepository;

    @Autowired
    private HistoryModelRepository historyModelRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    public RecordTag read(long tagId) throws DatabaseException {
        return tagRepository.findById(tagId)
                .map(entity -> entity.toRecord())
                .orElseThrow(() -> new DatabaseException("Tag with id:" + tagId + " not found!"));
    }

    @Override
    public RecordTag insert(long historyId, String tag) throws DatabaseException {
        return tagRepository
                .save(TagModel.builder().history(historyModelRepository.findById(historyId).get()).tag(tag).build())
                .toRecord();
    }

    @Override
    public void delete(long historyId, String tag) throws DatabaseException {
        tagRepository.deleteByHistoryIdAndTag(historyId, tag);
    }

    @Override
    public void deleteTagsForHistoryID(long historyId) throws DatabaseException {
        tagRepository.deleteByHistoryId(historyId);
    }

    @Override
    public List<RecordTag> getTagsForHistoryID(long historyId) throws DatabaseException {
        return tagRepository.findAllByHistoryId(historyId)
                .stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllTags() throws DatabaseException {
        return IterableUtils.toList(tagRepository.findAllTags());
    }
}
