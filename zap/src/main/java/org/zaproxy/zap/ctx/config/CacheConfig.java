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
    public org.springframework.cache.CacheManager cacheManager() throws IOException {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jCacheManager());
        cacheManager.setTransactionAware(true);
        return cacheManager;
    }

    // TODO: exposing cache stats via JMX is not working at the moment
    @Bean(destroyMethod = "close")
    public CacheManager jCacheManager() throws IOException {
        CachingProvider provider = Caching.getCachingProvider();
        return provider.getCacheManager(ehcacheConfig.getURI(), getClass().getClassLoader());
    }

}
