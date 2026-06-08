package com.example.campusdashboard.aspect;

import com.example.campusdashboard.annotation.RateLimit;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 * 基于Redis实现滑动窗口限流
 */
@Aspect
@Component
public class RateLimitAspect {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 在方法执行前进行限流检查
     */
    @Before("@annotation(rateLimit)")
    public void checkRateLimit(RateLimit rateLimit) {
        // 获取请求信息
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            return;
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 使用IP + URI作为限流key
        String ip = getClientIp(request);
        String uri = request.getRequestURI();
        String key = "rate_limit:" + ip + ":" + uri;
        
        int timeWindow = rateLimit.timeWindow();
        int maxRequests = rateLimit.maxRequests();
        
        // 获取当前请求次数
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == null) {
            return;
        }
        
        // 如果是第一次请求，设置过期时间
        if (count == 1) {
            redisTemplate.expire(key, timeWindow, TimeUnit.SECONDS);
        }
        
        // 超过限制，抛出异常
        if (count > maxRequests) {
            throw new RuntimeException("请求过于频繁，请在" + timeWindow + "秒后再试");
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
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
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
