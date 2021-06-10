package org.zaproxy.zap.ctx.config;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:zap.properties")
@ComponentScan(basePackages = "org.zaproxy")
@EnableJpaRepositories("org.zaproxy.zap.db")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING, defaultDomain = "org.zaproxy")
public class ZapConfig {

    // TODO: make configurable via ZAP
    // TODO: addtional drivers for posgre, mssql, mysql, mariadb ...
    // TODO: clean up Old Paros classes and old DB handling
    // TODO: enable extensions to use Liquibase migration scripts and Spring
    // Repoitories + Autowired

    @Bean
    public MBeanServer mBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
