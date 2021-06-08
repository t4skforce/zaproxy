package org.zaproxy.zap.ctx.config;

import java.io.IOException;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("classpath:ehcache.xml")
    private Resource ehcacheConfig;

    @Bean
    public JCacheCacheManager jCacheCacheManager() throws IOException {
        return new JCacheCacheManager(cacheManager());
    }

    // TODO: exposing cachestats via JMX is not working at the moment
    @Bean(destroyMethod = "close")
    public CacheManager cacheManager() throws IOException {
        CachingProvider provider = Caching.getCachingProvider();
        return provider.getCacheManager(ehcacheConfig.getURI(), getClass().getClassLoader());
    }

}
