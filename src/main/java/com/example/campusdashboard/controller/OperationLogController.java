package com.example.campusdashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.annotation.RequireRole;
import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.entity.OperationLog;
import com.example.campusdashboard.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理", description = "操作日志查询接口")
@RestController
@RequestMapping("/api/log")
public class OperationLogController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志 - 仅管理员
     */
    @RequireRole({"ADMIN"})
    @Operation(summary = "分页查询操作日志", description = "分页获取操作日志列表，支持按用户、操作类型、模块筛选（仅管理员）")
    @GetMapping("/list")
    public Result<Map<String, Object>> listLogs(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operation,
            @Parameter(description = "模块名称") @RequestParam(required = false) String module) {
        
        Page<OperationLog> result = operationLogService.listLogs(page, size, userId, operation, module);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNum", result.getCurrent());
        data.put("pageSize", result.getSize());
        
        return Result.success(data);
    }
}
