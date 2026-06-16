package com.example.campusdashboard.controller;

import com.example.campusdashboard.annotation.OperationLogAnnotation;
import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.entity.User;
import com.example.campusdashboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户相关操作接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return Result.error("无法获取用户信息，请重新登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 隐藏密码
        user.setPassword(null);
        
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @OperationLogAnnotation(module = "用户管理", operation = "UPDATE", description = "更新个人信息")
    @Operation(summary = "更新个人信息", description = "更新当前用户的昵称、邮箱、手机号等信息")
    @PutMapping("/update")
    public Result<Void> updateUserInfo(
            @Validated @RequestBody Map<String, String> userInfo,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return Result.error("无法获取用户信息，请重新登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新允许的字段
        if (userInfo.containsKey("realName")) {
            user.setRealName(userInfo.get("realName"));
        }
        if (userInfo.containsKey("email")) {
            user.setEmail(userInfo.get("email"));
        }
        if (userInfo.containsKey("phone")) {
            user.setPhone(userInfo.get("phone"));
        }
        
        boolean success = userService.updateById(user);
        
        if (success) {
            return Result.success("更新成功", null);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 修改密码
     */
    @OperationLogAnnotation(module = "用户管理", operation = "UPDATE", description = "修改密码")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return Result.error("无法获取用户信息，请重新登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("旧密码错误");
        }
        
        // 验证新密码长度
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        // 加密并更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        boolean success = userService.updateById(user);
        
        if (success) {
            return Result.success("密码修改成功，请重新登录", null);
        } else {
            return Result.error("密码修改失败");
        }
    }
}
