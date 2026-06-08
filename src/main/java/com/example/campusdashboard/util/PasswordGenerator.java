package com.example.campusdashboard.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具 - 用于生成正确的BCrypt哈希值
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 为 admin 用户生成密码 "123456" 的哈希值
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("===========================================");
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希值: " + encodedPassword);
        System.out.println("===========================================");
        System.out.println("\n请在数据库中执行以下SQL更新语句:");
        System.out.println("UPDATE user SET password = '" + encodedPassword + "' WHERE username = 'admin';");
        System.out.println("===========================================");
        
        // 验证生成的密码是否正确
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("\n验证结果: " + (matches ? "✓ 正确" : "✗ 错误"));
        System.out.println("===========================================\n");
    }
}
