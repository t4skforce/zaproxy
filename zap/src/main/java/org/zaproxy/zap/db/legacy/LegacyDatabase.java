package org.zaproxy.zap.db.legacy;

import org.parosproxy.paros.db.AbstractDatabase;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseServer;
import org.parosproxy.paros.db.TableAlert;
import org.parosproxy.paros.db.TableContext;
import org.parosproxy.paros.db.TableHistory;
import org.parosproxy.paros.db.TableParam;
import org.parosproxy.paros.db.TableScan;
import org.parosproxy.paros.db.TableSession;
import org.parosproxy.paros.db.TableSessionUrl;
import org.parosproxy.paros.db.TableStructure;
import org.parosproxy.paros.db.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LegacyDatabase extends AbstractDatabase {

    @Autowired
    private TableHistory legacyTableHistory;

    @Autowired
    private TableAlert leagcyTableAlert;

    @Override
    public DatabaseServer getDatabaseServer() {
        return new DatabaseServer() {
        };
    }

    @Override
    public TableHistory getTableHistory() {
        return legacyTableHistory;
    }

    @Override
    public TableSession getTableSession() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void open(String path) throws ClassNotFoundException, Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteSession(String sessionName) {
        // TODO Auto-generated method stub

    }

    @Override
    public TableAlert getTableAlert() {
        return leagcyTableAlert;
    }

    @Override
    public void setTableAlert(TableAlert tableAlert) {
        // TODO Auto-generated method stub

    }

    @Override
    public TableScan getTableScan() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTableScan(TableScan tableScan) {
        // TODO Auto-generated method stub

    }

    @Override
    public TableTag getTableTag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTableTag(TableTag tableTag) {
        // TODO Auto-generated method stub

    }

    @Override
    public TableSessionUrl getTableSessionUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTableSessionUrl(TableSessionUrl tableSessionUrl) {
        // TODO Auto-generated method stub

    }

    @Override
    public TableParam getTableParam() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TableContext getTableContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TableStructure getTableStructure() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void discardSession(long sessionId) throws DatabaseException {
        // TODO Auto-generated method stub

    }

}
