package org.zaproxy.zap.ctx.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "org.zaproxy")
@EnableJpaRepositories("org.zaproxy.zap.db")
@PropertySource("classpath:zap.properties")
@EnableTransactionManagement
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
        config.setProperty("hibernate.jdbc.time_zone", "UTC");
        config.setProperty("hibernate.jdbc.batch_size", "30");
        config.setProperty("hibernate.jdbc.fetch_size", "100");
        config.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        config.setProperty("hibernate.order_inserts", "true");
        config.setProperty("hibernate.order_updates", "true");
        config.setProperty("hibernate.id.new_generator_mappings", "true");
        config.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        config.setProperty("hibernate.connection.autocommit", "true");

        config.setProperty("hibernate.show_sql", "false");
        config.setProperty("hibernate.format_sql", "false");
        config.setProperty("hibernate.use_sql_comments", "false");

        config.setProperty("hibernate.query.plan_cache_max_size", "4096");
        config.setProperty("hibernate.cache.use_second_level_cache", "true");
        config.setProperty("hibernate.cache.use_query_cache", "true");
        config.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        config.setProperty("hibernate.cache.ehcache.missing_cache_strategy", "create");

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

}
