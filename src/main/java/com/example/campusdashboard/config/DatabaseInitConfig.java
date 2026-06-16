package com.example.campusdashboard.config;

import lombok.extern.slf4j.Slf4j;
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

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                log.info("开始初始化数据库表结构...");
                
                // 执行 schema.sql
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/schema.sql"));
                
                log.info("数据库表结构初始化完成！");
            } catch (Exception e) {
                log.error("数据库初始化失败", e);
                throw new RuntimeException("数据库初始化失败", e);
            }
        };
    }
}
