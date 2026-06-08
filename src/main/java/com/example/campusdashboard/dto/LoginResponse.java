package com.example.campusdashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    
    @Schema(description = "Token类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "用户角色", example = "USER")
    private String role;
}
