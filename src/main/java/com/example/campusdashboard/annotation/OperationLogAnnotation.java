package com.example.campusdashboard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 用于记录用户的操作行为
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogAnnotation {
    /**
     * 模块名称
     */
    String module();
    
    /**
     * 操作类型：ADD/UPDATE/DELETE/QUERY
     */
    String operation();
    
    /**
     * 操作描述
     */
    String description() default "";
}
