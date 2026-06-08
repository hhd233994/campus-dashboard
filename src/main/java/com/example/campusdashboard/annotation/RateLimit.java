package com.example.campusdashboard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * 用于防止接口被频繁调用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 时间窗口（秒）
     */
    int timeWindow() default 60;
    
    /**
     * 最大请求次数
     */
    int maxRequests() default 100;
}
