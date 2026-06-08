package com.example.campusdashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 主要用于提供BCrypt密码加密功能
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 配置密码编码器（BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置安全过滤链
     * 由于我们使用JWT进行认证，所以禁用Spring Security的默认认证机制
     * 但明确指定公开端点，遵循最小权限原则
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT，不需要CSRF保护）
            .csrf(csrf -> csrf.disable())
            // 禁用表单登录
            .formLogin(form -> form.disable())
            // 禁用HTTP Basic认证
            .httpBasic(basic -> basic.disable())
            // 明确指定公开端点，其他请求需要认证（通过JWT拦截器）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/",
                    "/index.html",
                    "/doc.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
