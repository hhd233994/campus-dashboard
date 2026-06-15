package com.example.campusdashboard.aspect;

import com.example.campusdashboard.annotation.RateLimit;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接口限流切面
 * 基于内存实现滑动窗口限流（替代Redis）
 */
@Aspect
@Component
public class RateLimitAspect {
    
    // 使用ConcurrentHashMap存储限流计数：key -> (count, expireTime)
    private final ConcurrentHashMap<String, LimitInfo> limitMap = new ConcurrentHashMap<>();
    
    /**
     * 限流信息内部类
     */
    private static class LimitInfo {
        AtomicLong count;
        long expireTime;
        
        LimitInfo(long expireTime) {
            this.count = new AtomicLong(0);
            this.expireTime = expireTime;
        }
    }
    
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
        long currentTime = System.currentTimeMillis();
        long expireTime = currentTime + timeWindow * 1000;
        
        // 获取或创建限流信息
        LimitInfo info = limitMap.computeIfAbsent(key, k -> new LimitInfo(expireTime));
        
        // 如果已过期，重置计数
        if (currentTime > info.expireTime) {
            info.count.set(0);
            info.expireTime = expireTime;
        }
        
        // 增加计数
        long count = info.count.incrementAndGet();
        
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
