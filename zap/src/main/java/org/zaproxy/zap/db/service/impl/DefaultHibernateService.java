package org.zaproxy.zap.db.service.impl;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.zaproxy.zap.ctx.config.CacheConfig;
import org.zaproxy.zap.db.service.HibernateService;
import org.zaproxy.zap.db.service.LiquibaseService;

import liquibase.exception.DatabaseException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Configuration
public class DefaultHibernateService implements HibernateService {

    private final Logger LOG = LogManager.getLogger(DefaultHibernateService.class);

    @Value("${hibernate.jdbc.time_zone:UTC}")
    private String jdbcTimeZone;

    @Value("${hibernate.jdbc.batch_size:30}")
    private Integer jdbcBatchSize;

    @Value("${hibernate.jdbc.fetch_size:100}")
    private Integer jdbcFetchSize;

    @Value("${hibernate.jdbc.batch_versioned_data:true}")
    private Boolean jdbcBatchVersionedData;

    @Value("${hibernate.order_inserts:true}")
    private Boolean orderInserts;

    @Value("${hibernate.order_updates:true}")
    private Boolean orderUpdates;

    @Value("${hibernate.id.new_generator_mappings:true}")
    private Boolean idNewIdGeneratorMappings;

    @Value("${hibernate.enable_lazy_load_no_trans:true}")
    private Boolean enableLazyLoadNoTrans;

    @Value("${hibernate.connection.autocommit:true}")
    private Boolean connectionAutocommit;

    @Value("${hibernate.show_sql:false}")
    private Boolean showSql;

    @Value("${hibernate.format_sql:false}")
    private Boolean formatSql;

    @Value("${hibernate.use_sql_comments:false}")
    private Boolean useSqlComments;

    @Value("${hibernate.generate_statistics:false}")
    private Boolean generateStatistics;

    @Value("${hibernate.jmx.enabled:true}")
    private Boolean jmxEnabled;

    @Value("${hibernate.jmx.usePlatformServer:true}")
    private Boolean jmxUsePlatformServer;

    @Value("${hibernate.query.plan_cache_max_size:4096}")
    private Integer queryPlanCacheMaxSize;

    @Value("${hibernate.cache.use_second_level_cache:true}")
    private Boolean cacheUseSecondLevelCache;

    @Value("${hibernate.cache.use_query_cache:true}")
    private Boolean cacheUseQueryCache;

    @Value("${hibernate.dialect:org.hibernate.dialect.HSQLDialect}")
    private String dialect;

    @Value("${hibernate.pool.initial_size:3}")
    private Integer poolInitialSize;

    @Value("${hibernate.connection.pool.max_idle:3}")
    private Integer poolMaxIdle;

    @Value("${hibernate.connection.pool.min_idle:0}")
    private Integer poolMinIdle;

    @Value("${hibernate.connection.pool.max_total:20}")
    private Integer poolMaxTotal;

    @Value("${hibernate.connection.pool.max_wait:-1}")
    private Integer poolMaxWaitMillis;

    @Value("${hibernate.connection.pool.prepared_statements:true}")
    private Boolean poolPreparedStatements;

    @Value("${hibernate.jdbc.driver:org.hsqldb.jdbc.JDBCDriver}")
    private String jdbcDriverClassName;

    @Value("${hibernate.jdbc.url:jdbc:hsqldb:mem:temp}")
    private String jdbcUrl;

    @Value("${hibernate.jdbc.username:sa}")
    private String jdbcUsername;

    @Value("${hibernate.jdbc.password:}")
    private String jdbcPassword;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private boolean migrationRequired = false;

    @Autowired
    private LiquibaseService liquibase;

    @Autowired
    private CacheConfig cacheConfig;

    @Bean
    public DataSource dataSource() {
        return configure(new BasicDataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // will set the provider to 'org.hibernate.ejb.HibernatePersistence'
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.zaproxy.zap.db");
        factory.setDataSource(dataSource());

        Properties properties = properties();
        // inital launch must ignore schema
        properties.setProperty(Environment.HBM2DDL_AUTO, "none");
        factory.setJpaProperties(properties);

        try {
            liquibase.update();
        } catch (DatabaseException e) {
            LOG.error(e.getMessage(), e);
        }

        // This will trigger the creation of the entity manager factory
        factory.afterPropertiesSet();

        return factory;
    }

    @Override
    public synchronized void apply() throws SQLException {
        // Stop DataSource and HibernateManagerFactory
        stop();

        // configure DataSource
        configure((BasicDataSource) dataSource());

        // configure Hibernate properties
        entityManagerFactory().setJpaProperties(properties());

        // Start DataSource
        start();
    }

    @Override
    public String getType() {
        try {
            return URI.create(StringUtils.removeStartIgnoreCase(jdbcUrl, "jdbc:")).getScheme().toLowerCase();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void start() throws SQLException {
        ((BasicDataSource) dataSource()).start();

        // Create/Migrate Schema
        LOG.info("Migration of DB Schema started.");
        try {
            liquibase.update();
        } catch (DatabaseException e) {
            throw new SQLException(e);
        }
        LOG.info("Migration of DB Schema finished.");

        entityManagerFactory().afterPropertiesSet();
    }

    @Override
    public void stop() throws SQLException {
        ((BasicDataSource) dataSource()).close();
        entityManagerFactory().destroy();
    }

    private BasicDataSource configure(BasicDataSource ds) {
        ds.setDriverClassName(jdbcDriverClassName);
        ds.setUrl(jdbcUrl);
        ds.setUsername(jdbcUsername);
        ds.setPassword(jdbcPassword);
        ds.setInitialSize(poolInitialSize);
        ds.setMaxIdle(poolMaxIdle);
        ds.setMinIdle(poolMinIdle);
        ds.setMaxTotal(poolMaxTotal);
        ds.setMaxWaitMillis(poolMaxWaitMillis);
        ds.setPoolPreparedStatements(poolPreparedStatements);
        return ds;
    }

    private Properties properties() {
        Properties config = new Properties();
        config.setProperty(Environment.DIALECT, dialect);
        config.setProperty(Environment.JDBC_TIME_ZONE, jdbcTimeZone);
        config.setProperty(Environment.STATEMENT_BATCH_SIZE, jdbcBatchSize.toString());
        config.setProperty(Environment.STATEMENT_FETCH_SIZE, jdbcFetchSize.toString());
        config.setProperty(Environment.BATCH_VERSIONED_DATA, jdbcBatchVersionedData.toString());
        config.setProperty(Environment.ORDER_INSERTS, orderInserts.toString());
        config.setProperty(Environment.ORDER_UPDATES, orderUpdates.toString());
        config.setProperty(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, idNewIdGeneratorMappings.toString());
        config.setProperty(Environment.ENABLE_LAZY_LOAD_NO_TRANS, enableLazyLoadNoTrans.toString());
        config.setProperty(Environment.AUTOCOMMIT, connectionAutocommit.toString());
        // auto generation of table structure is being done via liquibase
        // now we validate it
        config.setProperty(Environment.HBM2DDL_AUTO, "validate");

        config.setProperty(Environment.SHOW_SQL, showSql.toString());
        config.setProperty(Environment.FORMAT_SQL, formatSql.toString());
        config.setProperty(Environment.USE_SQL_COMMENTS, useSqlComments.toString());
        config.setProperty(Environment.GENERATE_STATISTICS, generateStatistics.toString());
        config.setProperty(Environment.JMX_ENABLED, jmxEnabled.toString());
        config.setProperty(Environment.JMX_PLATFORM_SERVER, jmxUsePlatformServer.toString());

        config.setProperty(Environment.QUERY_PLAN_CACHE_MAX_SIZE, queryPlanCacheMaxSize.toString());
        config.setProperty(Environment.USE_SECOND_LEVEL_CACHE, cacheUseSecondLevelCache.toString());
        config.setProperty(Environment.USE_QUERY_CACHE, cacheUseQueryCache.toString());
        config.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory");
        config.setProperty(Environment.CACHE_REGION_PREFIX, "zap");
        config.setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        config.setProperty("hibernate.javax.cache.missing_cache_strategy", "create");
        try {
            config.setProperty("hibernate.javax.cache.uri", cacheConfig.getEhcacheConfig().getURI().toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return config;
    }
}
