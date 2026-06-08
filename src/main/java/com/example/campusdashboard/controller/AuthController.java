package com.example.campusdashboard.controller;

import com.example.campusdashboard.annotation.OperationLogAnnotation;
import com.example.campusdashboard.annotation.RateLimit;
import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.dto.LoginRequest;
import com.example.campusdashboard.dto.LoginResponse;
import com.example.campusdashboard.dto.RegisterRequest;
import com.example.campusdashboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "用户认证", description = "用户登录、注册等认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @RateLimit(timeWindow = 60, maxRequests = 10) // 每分钟最多10次登录请求
    @OperationLogAnnotation(module = "用户认证", operation = "QUERY", description = "用户登录")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request.getUsername(), request.getPassword());
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @RateLimit(timeWindow = 3600, maxRequests = 5) // 每小时最多5次注册请求
    @OperationLogAnnotation(module = "用户认证", operation = "ADD", description = "用户注册")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        try {
            boolean success = userService.register(request);
            if (success) {
                return Result.success("注册成功", null);
            } else {
                return Result.error("注册失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
