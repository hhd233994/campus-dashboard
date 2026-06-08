package com.example.campusdashboard.service;

import com.example.demo.DemoApplication;
import com.example.campusdashboard.dto.LoginResponse;
import com.example.campusdashboard.dto.RegisterRequest;
import com.example.campusdashboard.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 */
@SpringBootTest(classes = DemoApplication.class)
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void testLogin() {
        // 执行测试 - 使用测试账号
        LoginResponse response = userService.login("admin", "123456");
        
        // 验证结果
        assertNotNull(response, "登录响应不应该为空");
        assertNotNull(response.getToken(), "Token不应该为空");
        assertEquals("admin", response.getUsername(), "用户名应该一致");
        assertEquals("ADMIN", response.getRole(), "角色应该一致");
    }
    
    @Test
    void testLoginWithWrongPassword() {
        // 执行测试 - 错误密码
        assertThrows(RuntimeException.class, () -> {
            userService.login("admin", "wrongpassword");
        }, "错误密码应该抛出异常");
    }
    
    @Test
    void testLoginWithNonExistentUser() {
        // 执行测试 - 不存在的用户
        assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent", "123456");
        }, "不存在的用户应该抛出异常");
    }
    
    @Test
    @Transactional
    void testRegister() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser" + System.currentTimeMillis());
        request.setPassword("123456");
        request.setRealName("测试用户");
        request.setEmail("test@example.com");
        request.setPhone("13800138003");
        
        // 执行测试
        boolean result = userService.register(request);
        
        // 验证结果
        assertTrue(result, "注册应该成功");
    }
    
    @Test
    void testRegisterWithDuplicateUsername() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin"); // 已存在的用户名
        request.setPassword("123456");
        request.setRealName("测试用户");
        
        // 执行测试
        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        }, "重复用户名应该抛出异常");
    }
    
    @Test
    void testGetUserByUsername() {
        // 执行测试
        User user = userService.getUserByUsername("admin");
        
        // 验证结果
        assertNotNull(user, "用户不应该为空");
        assertEquals("admin", user.getUsername(), "用户名应该一致");
        assertEquals("ADMIN", user.getRole(), "角色应该一致");
    }
    
    @Test
    void testGetNonExistentUser() {
        // 执行测试
        User user = userService.getUserByUsername("nonexistent");
        
        // 验证结果
        assertNull(user, "不存在的用户应该返回null");
    }
}
