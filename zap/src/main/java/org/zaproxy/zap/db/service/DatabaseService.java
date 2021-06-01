package org.zaproxy.zap.db.service;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

@Service
@Transactional(rollbackFor = { DatabaseException.class })
public class DatabaseService extends AbstractDatabase {

    private final Logger LOG = LogManager.getLogger(DatabaseService.class);

    private static final String HSQLDB = "hsqldb";

    private static final String HSQLDB_SHUTDOWN = "SHUTDOWN";

    private static final String HSQLDB_SHUTDOWN_COMPACT = "SHUTDOWN COMPACT";

    private static final String SQLITE = "sqlite";

    private static final String SQLITE_VACUUM = "VACUUM";

    private static final List<String> FILE_DB = Arrays.asList(HSQLDB, SQLITE);

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
    private DataSource dataSource;

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
            DriverManagerDataSource ds = dataSource.unwrap(DriverManagerDataSource.class);
            ds.setUrl(String.format("jdbc:hsqldb:file:%s;hsqldb.default_table_type=cached",
                    Paths.get(path).toAbsolutePath().toString().replaceAll("\\\\", "/")));

            // Initialize Liquibase and run the update
            try (java.sql.Connection conn = dataSource.getConnection()) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
                try (Liquibase liquibase = new Liquibase("db/changelog/master.xml", new ClassLoaderResourceAccessor(),
                        database)) {
                    liquibase.update("init");
                }
            }

            notifyListenersDatabaseOpen(databaseServer);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
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
        try {
            String[] parts = StringUtils.split(dataSource.unwrap(DriverManagerDataSource.class).getUrl(), ':');
            if (parts.length >= 2) {
                return parts[1];
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "error";
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

            // shutdown compact for hsqldb
            String type = getType();
            if (HSQLDB.equalsIgnoreCase(type)) {
                entityManager.createNativeQuery(compact ? HSQLDB_SHUTDOWN_COMPACT : HSQLDB_SHUTDOWN).executeUpdate();
            } else if (SQLITE.equalsIgnoreCase(type) && compact) {
                entityManager.createNativeQuery(SQLITE_VACUUM).executeUpdate();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
