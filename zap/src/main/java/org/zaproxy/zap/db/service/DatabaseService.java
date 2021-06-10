package org.zaproxy.zap.db.service;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class DatabaseService extends AbstractDatabase {

    private final Logger LOG = LogManager.getLogger(DatabaseService.class);

    private String type = "hsqldb";

    @Autowired
    private DatabaseServer databaseServer;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TableHistory tableHistory;

    @Autowired
    private TableAlert tableAlert;

    @Autowired
    private TableSession tableSession;

    @Autowired
    private TableScan tableScan;

    @Autowired
    private TableTag tableTag;

    @Autowired
    private TableSessionUrl tableSessionUrl;

    @Autowired
    private TableParam tableParam;

    @Autowired
    private TableContext tableContext;

    @Autowired
    private TableStructure tableStructure;

    @Autowired
    private Environment env;

    @Autowired
    private HibernateService hibernate;

    @Override
    public DatabaseServer getDatabaseServer() {
        return databaseServer;
    }

    @Override
    public TableHistory getTableHistory() {
        return tableHistory;
    }

    @Override
    public TableSession getTableSession() {
        return tableSession;
    }

    @Override
    public void open(String path) throws ClassNotFoundException, Exception {
        try {
            // DriverManagerDataSource ds =
            // dataSource.unwrap(DriverManagerDataSource.class);

            setType("hsqldb");

            Map<String, String> params = new HashedMap();
            params.put("type", getType());
            params.put("path", Paths.get(path).toAbsolutePath().toString().replaceAll("\\\\", "/"));
            params.put("url", path);
            params.put("name", path);

            hibernate.setDialect(getProperty("dialect", hibernate.getDialect()))
                    .setJdbcDriverClassName(getProperty("driver", hibernate.getJdbcDriverClassName()))
                    .setJdbcUrl(getProperty("url", hibernate.getJdbcUrl(), params))
                    .setJdbcUsername(getProperty("username", "sa"))
                    .setJdbcPassword(getProperty("password", ""))
                    .apply();

            notifyListenersDatabaseOpen(databaseServer);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    @Override
    public void moveSessionDb(String destFile) throws Exception {
        // TODO Auto-generated method stub
        super.moveSessionDb(destFile);
    }

    @Override
    public void copySessionDb(String currentFile, String destFile) throws Exception {
        // TODO Auto-generated method stub
        super.copySessionDb(currentFile, destFile);
    }

    @Override
    public void snapshotSessionDb(String currentFile, String destFile) throws Exception {
        // TODO Auto-generated method stub
        super.snapshotSessionDb(currentFile, destFile);
    }

    @Override
    public void createAndOpenUntitledDb() throws Exception {
        // TODO Auto-generated method stub
        super.createAndOpenUntitledDb();
    }

    @Override
    public void deleteSession(String sessionName) {
        // NOP
        // tableSession.
    }

    @Override
    public TableAlert getTableAlert() {
        return tableAlert;
    }

    @Override
    public void setTableAlert(TableAlert tableAlert) {
        // NOP
    }

    @Override
    public TableScan getTableScan() {
        return tableScan;
    }

    @Override
    public void setTableScan(TableScan tableScan) {
        // NOP
    }

    @Override
    public TableTag getTableTag() {
        return tableTag;
    }

    @Override
    public void setTableTag(TableTag tableTag) {
        // NOP
    }

    @Override
    public TableSessionUrl getTableSessionUrl() {
        return tableSessionUrl;
    }

    @Override
    public void setTableSessionUrl(TableSessionUrl tableSessionUrl) {
        // NOP
    }

    @Override
    public TableParam getTableParam() {
        return tableParam;
    }

    @Override
    public TableContext getTableContext() {
        return tableContext;
    }

    @Override
    public TableStructure getTableStructure() {
        return tableStructure;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void discardSession(long sessionId) throws DatabaseException {
        tableHistory.deleteHistorySession(sessionId);
    }

    @Override
    public void close(boolean compact) {
        this.close(compact, false);
    }

    @Override
    public void close(boolean compact, boolean cleanup) {
        super.close(compact, cleanup);
        try {
            if (cleanup) {
                // perform clean up
                tableHistory.deleteTemporary();
            }

            // shutdown compact
            if (compact) {
                Optional.ofNullable(getProperty("compact")).ifPresent(sql -> {
                    entityManager.createNativeQuery(sql).executeUpdate();
                });
            } else {
                Optional.ofNullable(getProperty("shutdown")).ifPresent(sql -> {
                    entityManager.createNativeQuery(sql).executeUpdate();
                });
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private String getProperty(String key, Map<String, String> params) {
        return getProperty(key, "", params);
    }

    private String getProperty(String key, String defaultValue, Map<String, String> params) {
        return StringSubstitutor.replace(env.getProperty(String.format("db.%s.%s", getType(), key), defaultValue),
                params, "{", "}");
    }

    private String getProperty(String key) {
        return env.getProperty(String.format("db.%s.%s", getType(), key));
    }

    private String getProperty(String key, String defaultValue) {
        return env.getProperty(String.format("db.%s.%s", getType(), key), defaultValue);
    }

    private <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(String.format("db.%s.%s", getType(), key), targetType, defaultValue);
    }
}
