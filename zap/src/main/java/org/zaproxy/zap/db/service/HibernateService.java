package org.zaproxy.zap.db.service;

import java.sql.SQLException;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import lombok.NonNull;

@ManagedResource(objectName = "org.zaproxy.zap:type=Database,name=Settings", description = "Manage Database Settings")
public interface HibernateService {

    /**
     * Applies new configuration to the Hibernate layer, and runs DB migration on
     * new DB using Liquibase
     *
     * @return HibernateService
     */
    @ManagedOperation(description = "Apply Configuration changes")
    void apply() throws SQLException;

    @ManagedOperation(description = "Starts Hibernate Layer if currently not running")
    void start() throws SQLException;

    @ManagedOperation(description = "Stops Hibernate Layer if currently not running")
    void stop() throws SQLException;

    @ManagedAttribute(description = "Currently used DB type")
    String getType();

    // Settings
    @ManagedAttribute(description = "Default JDBC TimeZone")
    String getJdbcTimeZone();

    @ManagedAttribute
    HibernateService setJdbcTimeZone(@NonNull String value);

    @ManagedAttribute(description = "The fully qualified Java class name of the JDBC driver to be used.")
    String getJdbcDriverClassName();

    @ManagedAttribute
    HibernateService setJdbcDriverClassName(@NonNull String value);

    @ManagedAttribute(description = "The connection URL to be passed to our JDBC driver to establish a connection.")
    String getJdbcUrl();

    @ManagedAttribute
    HibernateService setJdbcUrl(@NonNull String value);

    @ManagedAttribute(description = "The connection user name to be passed to our JDBC driver to establish a connection.")
    String getJdbcUsername();

    @ManagedAttribute
    HibernateService setJdbcUsername(@NonNull String value);

    @ManagedAttribute(description = "The connection password to be passed to our JDBC driver to establish a connection.")
    String getJdbcPassword();

    @ManagedAttribute(description = "Maximum JDBC batch size. A nonzero value enables batch updates.")
    Integer getJdbcBatchSize();

    @ManagedAttribute
    HibernateService setJdbcBatchSize(@NonNull Integer value);

    @ManagedAttribute(description = "Gives the JDBC driver a hint as to the number of rows that should be fetched from the database when more rows are needed. If 0, JDBC driver default settings will be used.")
    Integer getJdbcFetchSize();

    @ManagedAttribute
    HibernateService setJdbcFetchSize(@NonNull Integer value);

    @ManagedAttribute(description = "Should versioned data be included in batching?")
    Boolean getJdbcBatchVersionedData();

    @ManagedAttribute
    HibernateService setJdbcPassword(@NonNull String value);

    @ManagedAttribute
    HibernateService setJdbcBatchVersionedData(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable ordering of insert statements for the purpose of more efficient JDBC batching.")
    Boolean getOrderInserts();

    @ManagedAttribute
    HibernateService setOrderInserts(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable ordering of update statements by primary key value.")
    Boolean getOrderUpdates();

    @ManagedAttribute
    HibernateService setOrderUpdates(@NonNull Boolean value);

    @ManagedAttribute(description = "Setting which indicates whether or not the new IdentifierGenerator is used for AUTO, TABLE and SEQUENCE.")
    Boolean getIdNewIdGeneratorMappings();

    @ManagedAttribute
    HibernateService setIdNewIdGeneratorMappings(@NonNull Boolean value);

    @ManagedAttribute(description = "Enables Lazy Loading to work by creating a Session for each request to the lazy loaded resource.")
    Boolean getEnableLazyLoadNoTrans();

    @ManagedAttribute
    HibernateService setEnableLazyLoadNoTrans(@NonNull Boolean value);

    @ManagedAttribute(description = "Controls the autocommit mode of JDBC Connections obtained.")
    Boolean getConnectionAutocommit();

    @ManagedAttribute
    HibernateService setConnectionAutocommit(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable logging of generated SQL to the console.")
    Boolean getShowSql();

    @ManagedAttribute
    HibernateService setShowSql(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable formatting of SQL logged to the console.")
    Boolean getFormatSql();

    @ManagedAttribute
    HibernateService setFormatSql(@NonNull Boolean value);

    @ManagedAttribute(description = "Add comments to the generated SQL.")
    Boolean getUseSqlComments();

    @ManagedAttribute
    HibernateService setUseSqlComments(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable statistics collection.")
    Boolean getGenerateStatistics();

    @ManagedAttribute
    HibernateService setGenerateStatistics(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable JMX exposure of Hibernate.")
    Boolean getJmxEnabled();

    @ManagedAttribute
    HibernateService setJmxEnabled(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable Hibernate to use the default JMX Platform Server.")
    Boolean getJmxUsePlatformServer();

    @ManagedAttribute
    HibernateService setJmxUsePlatformServer(@NonNull Boolean value);

    @ManagedAttribute(description = "The maximum number of entries in Query Plan Cache.")
    Integer getQueryPlanCacheMaxSize();

    @ManagedAttribute
    HibernateService setQueryPlanCacheMaxSize(Integer value);

    @ManagedAttribute(description = "Enable the second-level cache.")
    Boolean getCacheUseSecondLevelCache();

    @ManagedAttribute
    HibernateService setCacheUseSecondLevelCache(@NonNull Boolean value);

    @ManagedAttribute(description = "Enable the query cache.")
    Boolean getCacheUseQueryCache();

    @ManagedAttribute
    HibernateService setCacheUseQueryCache(@NonNull Boolean value);

    @ManagedAttribute(description = "Names the Hibernate SQL Dialect class")
    String getDialect();

    @ManagedAttribute
    HibernateService setDialect(@NonNull String value);

    @ManagedAttribute(description = "The initial number of connections that are created when the pool is started.")
    Integer getPoolInitialSize();

    @ManagedAttribute
    HibernateService setPoolInitialSize(@NonNull Integer value);

    @ManagedAttribute(description = "The maximum number of connections that can remain idle in the pool.")
    Integer getPoolMaxIdle();

    @ManagedAttribute
    HibernateService setPoolMaxIdle(@NonNull Integer value);

    @ManagedAttribute(description = "The minimum number of active connections that can remain idle in the pool")
    Integer getPoolMinIdle();

    @ManagedAttribute
    HibernateService setPoolMinIdle(@NonNull Integer value);

    @ManagedAttribute(description = "The maximum number of active connections that can be allocated from this pool at the same time")
    Integer getPoolMaxTotal();

    @ManagedAttribute
    HibernateService setPoolMaxTotal(@NonNull Integer value);

    @ManagedAttribute(description = "The maximum number of milliseconds that the pool will wait for a vconnection to be returned before throwing an exception")
    Integer getPoolMaxWaitMillis();

    @ManagedAttribute
    HibernateService setPoolMaxWaitMillis(@NonNull Integer value);

    @ManagedAttribute(description = "Prepared statement pooling for this pool.")
    Boolean getPoolPreparedStatements();

    @ManagedAttribute
    HibernateService setPoolPreparedStatements(@NonNull Boolean value);
}
