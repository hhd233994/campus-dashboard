package com.example.campusdashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库初始化配置
 * 在应用启动时自动执行 schema.sql 创建表结构
 */
@Slf4j
@Configuration
public class DatabaseInitConfig {

    @Value("${spring.datasource.url:NOT_SET}")
    private String dbUrl;
    
    @Value("${spring.datasource.username:NOT_SET}")
    private String dbUsername;

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            // 打印数据库配置用于调试
            log.info("=== 数据库配置检查 ===");
            log.info("DB_URL: {}", dbUrl);
            log.info("DB_USERNAME: {}", dbUsername);
            log.info("=====================");
            
            try (Connection connection = dataSource.getConnection()) {
                log.info("开始初始化数据库表结构...");
                
                // 执行 schema.sql
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/schema.sql"));
                
                log.info("数据库表结构初始化完成！");
            } catch (Exception e) {
                log.warn("数据库初始化失败（可能表已存在），继续启动应用", e);
                // 不抛出异常，允许应用继续启动
            }
        };
    }
}
