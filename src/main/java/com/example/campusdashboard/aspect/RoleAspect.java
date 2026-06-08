package com.example.campusdashboard.aspect;

import com.example.campusdashboard.annotation.RequireRole;
import com.example.campusdashboard.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 角色权限切面
 * 拦截带有@RequireRole注解的方法，验证用户角色权限
 */
@Aspect
@Component
public class RoleAspect {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 在方法执行前进行角色验证
     */
    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        // 获取HTTP请求
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            throw new RuntimeException("无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 从请求头获取Token
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("未提供认证Token");
        }
        
        String token = authorization.substring(7);
        
        // 验证Token并获取用户角色
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }
        
        String userRole = jwtUtil.getRoleFromToken(token);
        String[] allowedRoles = requireRole.value();
        
        // 检查用户角色是否在允许的角色列表中
        if (!Arrays.asList(allowedRoles).contains(userRole)) {
            throw new RuntimeException("权限不足，需要角色: " + Arrays.toString(allowedRoles));
        }
    }
}
