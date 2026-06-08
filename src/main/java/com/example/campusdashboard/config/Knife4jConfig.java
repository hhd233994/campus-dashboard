package com.example.campusdashboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 */
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园消费数据管理平台 API 文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3 + MyBatis-Plus 的校园消费管理系统")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")));
    }
}
