package com.example.campusdashboard.util;

import com.example.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@SpringBootTest(classes = DemoApplication.class)
class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Test
    void testGenerateAndValidateToken() {
        // 生成Token
        String token = jwtUtil.generateToken("testuser", 1L, "USER");
        
        // 验证Token不为空
        assertNotNull(token, "生成的Token不应该为空");
        assertTrue(token.length() > 0, "Token长度应该大于0");
        
        // 验证Token有效性
        assertTrue(jwtUtil.validateToken(token), "Token应该有效");
    }
    
    @Test
    void testGetUsernameFromToken() {
        // 生成Token
        String username = "testuser";
        String token = jwtUtil.generateToken(username, 1L, "USER");
        
        // 从Token中获取用户名
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        
        // 验证用户名一致
        assertEquals(username, extractedUsername, "提取的用户名应该与原始用户名一致");
    }
    
    @Test
    void testGetUserIdFromToken() {
        // 生成Token
        Long userId = 123L;
        String token = jwtUtil.generateToken("testuser", userId, "USER");
        
        // 从Token中获取用户ID
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);
        
        // 验证用户ID一致
        assertEquals(userId, extractedUserId, "提取的用户ID应该与原始用户ID一致");
    }
    
    @Test
    void testGetRoleFromToken() {
        // 生成Token
        String role = "ADMIN";
        String token = jwtUtil.generateToken("admin", 1L, role);
        
        // 从Token中获取角色
        String extractedRole = jwtUtil.getRoleFromToken(token);
        
        // 验证角色一致
        assertEquals(role, extractedRole, "提取的角色应该与原始角色一致");
    }
    
    @Test
    void testInvalidToken() {
        // 测试无效Token
        String invalidToken = "invalid.token.here";
        
        // 验证Token无效
        assertFalse(jwtUtil.validateToken(invalidToken), "无效Token应该被识别");
    }
    
    @Test
    void testNullToken() {
        // 测试null Token
        assertFalse(jwtUtil.validateToken(null), "null Token应该返回false");
    }
}
