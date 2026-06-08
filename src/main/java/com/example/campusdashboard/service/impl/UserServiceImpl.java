package com.example.campusdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campusdashboard.dto.LoginResponse;
import com.example.campusdashboard.dto.RegisterRequest;
import com.example.campusdashboard.entity.User;
import com.example.campusdashboard.mapper.UserMapper;
import com.example.campusdashboard.service.UserService;
import com.example.campusdashboard.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResponse login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码（使用BCrypt加密）
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        
        if (!matches) {
            System.out.println("Login failed for user: " + username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        System.out.println("Login successful for user: " + username);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        
        return response;
    }
    
    @Override
    public boolean register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        User existUser = getUserByUsername(registerRequest.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // BCrypt加密密码
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRealName(registerRequest.getRealName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRole("USER"); // 默认普通用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
