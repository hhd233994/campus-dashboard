package com.example.campusdashboard.aspect;

import com.example.campusdashboard.annotation.OperationLogAnnotation;
import com.example.campusdashboard.entity.OperationLog;
import com.example.campusdashboard.mapper.OperationLogMapper;
import com.example.campusdashboard.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 记录用户的操作行为到数据库
 */
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 环绕通知，记录操作日志
     */
    @Around("@annotation(logAnnotation)")
    public Object recordLog(ProceedingJoinPoint joinPoint, OperationLogAnnotation logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 执行目标方法
        Object result = null;
        Integer statusCode = 200;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            statusCode = 500;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 异步记录日志（避免影响主业务流程）
            saveLog(joinPoint, logAnnotation, statusCode, duration);
        }
    }
    
    /**
     * 保存操作日志
     */
    private void saveLog(ProceedingJoinPoint joinPoint, OperationLogAnnotation logAnnotation, 
                        Integer statusCode, Long duration) {
        try {
            OperationLog log = new OperationLog();
            log.setModule(logAnnotation.module());
            log.setOperation(logAnnotation.operation());
            log.setDescription(logAnnotation.description());
            log.setStatusCode(statusCode);
            log.setDuration(duration);
            log.setCreateTime(LocalDateTime.now());
            
            // 获取请求信息
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setIp(getIpAddress(request));
                log.setMethod(request.getMethod() + " " + request.getRequestURI());
                
                // 从Token中获取用户信息
                String authorization = request.getHeader("Authorization");
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    String token = authorization.substring(7);
                    if (jwtUtil.validateToken(token)) {
                        log.setUserId(jwtUtil.getUserIdFromToken(token));
                        log.setUsername(jwtUtil.getUsernameFromToken(token));
                    }
                }
            }
            
            // 插入数据库
            operationLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不应影响主业务
            System.err.println("记录操作日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
