package com.example.campusdashboard.service;

import com.example.campusdashboard.dto.LoginResponse;
import com.example.campusdashboard.dto.RegisterRequest;
import com.example.campusdashboard.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录响应（包含Token）
     */
    LoginResponse login(String username, String password);
    
    /**
     * 用户注册
     * @param registerRequest 注册信息
     * @return 是否注册成功
     */
    boolean register(RegisterRequest registerRequest);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
}
