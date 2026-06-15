package com.example.campusdashboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存配置类（替代Redis）
 */
@Configuration
@EnableCaching
public class RedisConfig {
    
    /**
     * Caffeine缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)  // 最大缓存条目数
                .expireAfterWrite(1, TimeUnit.HOURS)  // 写入后1小时过期
                .recordStats());  // 记录统计信息
        return cacheManager;
    }
}
