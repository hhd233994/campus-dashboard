package com.example.campusdashboard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "密码（加密存储）", hidden = true)
    private String password;
    
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "用户角色：USER-普通用户, ADMIN-管理员", example = "USER")
    private String role;
    
    @Schema(description = "创建时间", example = "2026-04-25T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2026-04-25T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
