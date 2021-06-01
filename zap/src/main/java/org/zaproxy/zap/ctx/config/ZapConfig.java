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
@EnableJpaRepositories("org.zaproxy.zap.db")
@EnableTransactionManagement
public class ZapConfig {

    // TODO: make configurable via ZAP
    // TODO: addtional drivers for posgre, mssql, mysql, mariadb, sqlite ...
    // TODO: Work with liquibase context initialize for new db and migration for old
    // TODO: clean up Old Paros classes and old DB handling
    // TODO: enable extensions to use Liquibase migration scripts and Spring
    // Repoitories + Autowired
    @Lazy
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:temp");
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
        config.setProperty("hibernate.jdbc.time_zone", "UTC");
        config.setProperty("hibernate.jdbc.batch_size", "1000");
        config.setProperty("hibernate.jdbc.fetch_size", "1000");
        config.setProperty("hibernate.order_inserts", "true");
        config.setProperty("hibernate.order_updates", "true");
        config.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.format_sql", "false");
        config.setProperty("hibernate.use_sql_comments", "true");
        config.setProperty("hibernate.query.plan_cache_max_size", "4096");
        config.setProperty("hibernate.cache.use_second_level_cache", "true");
        config.setProperty("hibernate.cache.use_query_cache", "true");
        config.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        config.setProperty("hibernate.cache.ehcache.missing_cache_strategy", "create");

        // will set the provider to 'org.hibernate.ejb.HibernatePersistence'
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // will set hibernate.show_sql to 'true'
        vendorAdapter.setShowSql(true);
        // if set to true, will set hibernate.hbm2ddl.auto to 'update'
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.zaproxy.zap.db");
        factory.setDataSource(dataSource());
        factory.setJpaProperties(config);

        // This will trigger the creation of the entity manager factory
        factory.afterPropertiesSet();

        return factory.getObject();
    }

}
