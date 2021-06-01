package org.zaproxy.zap.db.dao;

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.db.RecordScan;
import org.parosproxy.paros.db.TableScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zaproxy.zap.db.model.ScanModel;
import org.zaproxy.zap.db.repository.ScanModelRepository;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class ScanDao implements TableScan {

    @Autowired
    private ScanModelRepository scanRepository;

    @Override
    public void databaseOpen(DatabaseServer dbServer) throws DatabaseException, DatabaseUnsupportedException {
        // NOP
    }

    @Override
    public RecordScan getLatestScan() throws DatabaseException {
        return scanRepository.findTopByOrderByIdDesc().map(entity -> entity.toRecord()).orElseGet(null);
    }

    @Override
    public RecordScan read(int scanId) throws DatabaseException {
        return scanRepository.findById((long) scanId)
                .map(entity -> entity.toRecord())
                .orElseThrow(() -> new DatabaseException("Scan with id:" + scanId + " doesn't exist!"));
    }

    @Override
    public RecordScan insert(long sessionId, String scanName) throws DatabaseException {
        ScanModel entity = ScanModel.builder().sessionId(sessionId).name(scanName).build();
        return scanRepository.save(entity).toRecord();
    }

}
