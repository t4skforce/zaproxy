package org.zaproxy.zap.db.service;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.db.AbstractDatabase;
import org.parosproxy.paros.db.Database;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class DatabaseService extends AbstractDatabase {

    private final Logger LOG = LogManager.getLogger(DatabaseService.class);

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

    private String type = Database.DB_TYPE_HSQLDB;

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
            setType("hsqldb");

            Map<String, String> params = new HashMap<>();
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
    public boolean isFileDb() {
        return BooleanUtils.isTrue(getProperty("fileBased", Boolean.class, Boolean.TRUE));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getFiles() {
        return getProperty("fileNames", List.class, Collections.EMPTY_LIST);
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
        return hibernate.getType();
    }

    private void setType(String type) {
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

    private String getProperty(String key, String defaultValue, Map<String, String> params) {
        return StringSubstitutor.replace(env.getProperty(String.format("db.%s.%s", type, key), defaultValue), params,
                "{", "}");
    }

    private String getProperty(String key) {
        return env.getProperty(String.format("db.%s.%s", type, key));
    }

    private String getProperty(String key, String defaultValue) {
        return env.getProperty(String.format("db.%s.%s", type, key), defaultValue);
    }

    @Nullable
    private <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(String.format("db.%s.%s", type, key), targetType, defaultValue);
    }

}
