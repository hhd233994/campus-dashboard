package com.example.campusdashboard.controller;

import com.example.campusdashboard.dto.LoginResponse;
import com.example.campusdashboard.dto.RegisterRequest;
import com.example.campusdashboard.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器测试类
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setToken("mock-jwt-token");
        mockResponse.setUsername("testuser");
        mockResponse.setRole("USER");

        when(userService.login(eq("testuser"), eq("password123"))).thenReturn(mockResponse);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("登录成功"))
            .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
            .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).login(eq("testuser"), eq("password123"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        // Given
        when(userService.login(eq("testuser"), eq("wrongpassword")))
            .thenThrow(new RuntimeException("用户名或密码错误"));

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(401));

        verify(userService, times(1)).login(eq("testuser"), eq("wrongpassword"));
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setRealName("新用户");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPhone("13800138000");

        when(userService.register(any(RegisterRequest.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_DuplicateUsername() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existinguser");
        registerRequest.setPassword("password123");

        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("用户名已存在"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }
}
