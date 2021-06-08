package org.zaproxy.zap.ctx.config;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:zap.properties")
@ComponentScan(basePackages = "org.zaproxy")
@EnableJpaRepositories("org.zaproxy.zap.db")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING, defaultDomain = "org.zaproxy")
public class ZapConfig {

    // TODO: make configurable via ZAP
    // TODO: addtional drivers for posgre, mssql, mysql, mariadb, sqlite ...
    // TODO: Work with liquibase context initialize for new db and migration for old
    // TODO: clean up Old Paros classes and old DB handling
    // TODO: enable extensions to use Liquibase migration scripts and Spring
    // Repoitories + Autowired
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        // DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:temp");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        // Connection pooling properties
        dataSource.setInitialSize(3);
        dataSource.setMaxIdle(3);
        dataSource.setMaxTotal(15);
        dataSource.setMinIdle(0);

        return dataSource;
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

    // TODO: Add config options for 2nd level cache of ehache
    // https://howtodoinjava.com/hibernate/hibernate-ehcache-configuration-tutorial/
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        Properties config = new Properties();
        config.setProperty(Environment.JDBC_TIME_ZONE, "UTC");
        config.setProperty(Environment.STATEMENT_BATCH_SIZE, "30");
        config.setProperty(Environment.STATEMENT_FETCH_SIZE, "100");
        config.setProperty(Environment.BATCH_VERSIONED_DATA, "true");
        config.setProperty(Environment.ORDER_INSERTS, "true");
        config.setProperty(Environment.ORDER_UPDATES, "true");
        config.setProperty(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, "true");
        config.setProperty(Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true");
        config.setProperty(Environment.AUTOCOMMIT, "true");

        config.setProperty(Environment.SHOW_SQL, "false");
        config.setProperty(Environment.FORMAT_SQL, "false");
        config.setProperty(Environment.USE_SQL_COMMENTS, "false");
        config.setProperty(Environment.GENERATE_STATISTICS, "true");
        config.setProperty(Environment.JMX_ENABLED, "true");
        config.setProperty(Environment.JMX_PLATFORM_SERVER, "true");
        config.setProperty(Environment.JMX_DEFAULT_OBJ_NAME_DOMAIN, "Database");

        config.setProperty(Environment.QUERY_PLAN_CACHE_MAX_SIZE, "4096");
        config.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
        config.setProperty(Environment.USE_QUERY_CACHE, "true");
        config.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory");
        config.setProperty(Environment.CACHE_REGION_PREFIX, "zap");
        config.setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        config.setProperty("hibernate.javax.cache.missing_cache_strategy", "create");

        // will set the provider to 'org.hibernate.ejb.HibernatePersistence'
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // will set hibernate.show_sql to 'true'
        vendorAdapter.setShowSql(false);
        // if set to true, will set hibernate.hbm2ddl.auto to 'update'
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.zaproxy.zap.db");
        factory.setDataSource(dataSource());
        factory.setJpaProperties(config);

        // This will trigger the creation of the entity manager factory
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public MBeanServer mBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

}
