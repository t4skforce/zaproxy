package org.zaproxy.zap.ctx.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "org.zaproxy")
@EnableJpaRepositories("org.zaproxy.zap.db.repository")
@EnableTransactionManagement
public class ZapConfig {

    // TODO: Dynamically change data source -
    // https://blog.virtual7.de/dynamically-change-data-source-connection-details-at-runtime-in-spring-boot/
    // TODO: make configurable via ZAP
    // TODO: Add DB-Versioning via liquibase -
    // https://www.liquibase.org/blog/3-ways-to-run-liquibase
    // https://auth0.com/blog/integrating-spring-data-jpa-postgresql-liquibase/
    // TODO: addtional drivers for posgre, mssql, mysql, mariadb, sqlite ...
    @Lazy
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:file:/home/username/Desktop/test_session/test.session");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Lazy
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }

    @Lazy
    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    // TODO: Add config options for 2nd level cache of ehache
    // https://howtodoinjava.com/hibernate/hibernate-ehcache-configuration-tutorial/
    @Lazy
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        Properties config = new Properties();
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.format_sql", "true");
        config.setProperty("hibernate.use_sql_comments", "true");
        config.setProperty("hibernate.cache.use_second_level_cache", "true");
        config.setProperty("hibernate.cache.use_query_cache", "true");
        config.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");

        // will set the provider to 'org.hibernate.ejb.HibernatePersistence'
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // will set hibernate.show_sql to 'true'
        vendorAdapter.setShowSql(true);
        // if set to true, will set hibernate.hbm2ddl.auto to 'update'
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.zaproxy.zap.db.schema");
        factory.setDataSource(dataSource());
        factory.setJpaProperties(config);

        // This will trigger the creation of the entity manager factory
        factory.afterPropertiesSet();

        return factory.getObject();
    }

}
