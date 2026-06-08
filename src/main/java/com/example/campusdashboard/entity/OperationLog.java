package com.example.campusdashboard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@Schema(description = "操作日志")
public class OperationLog {
    
    @Schema(description = "日志ID", example = "1")
    private Long id;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "操作类型", example = "ADD")
    private String operation; // ADD/UPDATE/DELETE/QUERY
    
    @Schema(description = "模块名称", example = "消费记录")
    private String module;
    
    @Schema(description = "操作描述", example = "新增消费记录")
    private String description;
    
    @Schema(description = "请求IP", example = "192.168.1.100")
    private String ip;
    
    @Schema(description = "请求方法", example = "POST /api/record/add")
    private String method;
    
    @Schema(description = "响应状态码", example = "200")
    private Integer statusCode;
    
    @Schema(description = "耗时（毫秒）", example = "45")
    private Long duration;
    
    @Schema(description = "创建时间", example = "2026-05-03T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
