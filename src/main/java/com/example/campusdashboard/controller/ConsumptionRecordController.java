package com.example.campusdashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.annotation.OperationLogAnnotation;
import com.example.campusdashboard.annotation.RequireRole;
import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.ConsumptionRecordExcel;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;
import com.example.campusdashboard.service.ConsumptionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费记录控制器
 */
@Tag(name = "消费记录管理", description = "消费记录的增删改查接口")
@RestController
@RequestMapping("/api/record")
public class ConsumptionRecordController {
    
    @Autowired
    private ConsumptionRecordService consumptionRecordService;
    
    /**
     * 新增消费记录
     */
    @RequireRole({"USER", "ADMIN"})
    @OperationLogAnnotation(module = "消费记录", operation = "ADD", description = "新增消费记录")
    @Operation(summary = "新增消费记录", description = "添加一条新的消费记录")
    @PostMapping("/add")
    public Result<ConsumptionRecord> addRecord(@Validated @RequestBody ConsumptionRecord record) {
        boolean success = consumptionRecordService.addRecord(record);
        
        if (success) {
            return Result.success("添加成功", record);
        } else {
            return Result.error("添加失败");
        }
    }
    
    /**
     * 查询所有记录（分页）- 仅管理员
     */
    @RequireRole({"ADMIN"})
    @Operation(summary = "分页查询消费记录", description = "分页获取所有消费记录列表，支持按日期和分类筛选（仅管理员）")
    @GetMapping("/list")
    public Result<Map<String, Object>> listRecords(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "消费类别") @RequestParam(required = false) String category) {
        Page<ConsumptionRecord> result = consumptionRecordService.listRecords(page, size, startDate, endDate, category);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNum", result.getCurrent());
        data.put("pageSize", result.getSize());
        
        return Result.success(data);
    }
    
    /**
     * 根据用户ID查询
     */
    @Operation(summary = "查询用户消费记录", description = "根据用户ID获取该用户的所有消费记录")
    @GetMapping("/user/{userId}")
    public Result<List<ConsumptionRecord>> getRecordsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<ConsumptionRecord> records = consumptionRecordService.getRecordsByUserId(userId);
        return Result.success(records);
    }
    
    /**
     * 删除记录 - 仅管理员
     */
    @RequireRole({"ADMIN"})
    @OperationLogAnnotation(module = "消费记录", operation = "DELETE", description = "删除消费记录")
    @Operation(summary = "删除消费记录", description = "根据ID删除指定消费记录（仅管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@Parameter(description = "记录ID") @PathVariable Long id) {
        boolean success = consumptionRecordService.deleteRecord(id);
        
        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 更新消费记录
     */
    @RequireRole({"USER", "ADMIN"})
    @OperationLogAnnotation(module = "消费记录", operation = "UPDATE", description = "更新消费记录")
    @Operation(summary = "更新消费记录", description = "根据ID更新消费记录信息")
    @PutMapping("/{id}")
    public Result<Void> updateRecord(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Validated @RequestBody ConsumptionRecord record) {
        record.setId(id);
        boolean success = consumptionRecordService.updateRecord(record);
        
        if (success) {
            return Result.success("更新成功", null);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 按分类统计
     */
    @Operation(summary = "分类统计", description = "按消费类别统计数据")
    @GetMapping("/statistics/category")
    public Result<List<CategoryStatistics>> getCategoryStatistics() {
        List<CategoryStatistics> statistics = consumptionRecordService.getCategoryStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 按日期统计（最近7天）
     */
    @Operation(summary = "每日统计", description = "最近7天的消费数据统计")
    @GetMapping("/statistics/daily")
    public Result<List<DailyStatistics>> getDailyStatistics() {
        List<DailyStatistics> statistics = consumptionRecordService.getDailyStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 获取总消费金额
     */
    @Operation(summary = "总消费金额", description = "获取所有消费记录的总金额")
    @GetMapping("/statistics/total")
    public Result<BigDecimal> getTotalAmount() {
        BigDecimal totalAmount = consumptionRecordService.getTotalAmount();
        return Result.success(totalAmount);
    }
    
    /**
     * 获取今日消费金额
     */
    @Operation(summary = "今日消费", description = "获取今天的消费总金额")
    @GetMapping("/statistics/today")
    public Result<BigDecimal> getTodayAmount() {
        BigDecimal todayAmount = consumptionRecordService.getTodayAmount();
        return Result.success(todayAmount);
    }
    
    /**
     * 获取概览数据
     */
    @Operation(summary = "数据概览", description = "获取包含总金额、今日金额、分类统计、每日统计的概览数据")
    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> getOverview(jakarta.servlet.http.HttpServletRequest request) {
        // 从request中获取用户角色
        String role = (String) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        Map<String, Object> overview;
        
        if ("ADMIN".equals(role)) {
            // 管理员查看所有数据
            overview = consumptionRecordService.getOverview();
        } else {
            // 普通用户只查看自己的数据
            overview = consumptionRecordService.getUserOverview(userId);
        }
        
        return Result.success(overview);
    }
    
    /**
     * 导出Excel - 仅管理员
     */
    @RequireRole({"ADMIN"})
    @Operation(summary = "导出Excel", description = "导出所有消费记录到Excel文件（仅管理员）")
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("消费记录_" + System.currentTimeMillis(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            
            // 查询所有数据
            List<ConsumptionRecord> records = consumptionRecordService.getRecordsByUserId(null);
            
            // 转换为Excel DTO
            List<ConsumptionRecordExcel> excelData = records.stream().map(record -> {
                ConsumptionRecordExcel excel = new ConsumptionRecordExcel();
                excel.setId(record.getId());
                excel.setUserId(record.getUserId());
                excel.setCategory(record.getCategory());
                excel.setAmount(record.getAmount());
                excel.setDescription(record.getDescription());
                excel.setCreateTime(record.getCreateTime());
                return excel;
            }).collect(java.util.stream.Collectors.toList());
            
            // 写入Excel
            EasyExcel.write(response.getOutputStream(), ConsumptionRecordExcel.class)
                    .sheet("消费记录")
                    .doWrite(excelData);
                    
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询当前用户的消费记录（分页）- 所有用户
     */
    @RequireRole({"USER", "ADMIN"})
    @Operation(summary = "查询我的消费记录", description = "分页获取当前登录用户的消费记录列表，支持按日期和分类筛选")
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyRecords(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "消费类别") @RequestParam(required = false) String category,
            jakarta.servlet.http.HttpServletRequest request) {
        // 从request中获取JWT拦截器存入的用户ID
        Long currentUserId = (Long) request.getAttribute("userId");
        
        if (currentUserId == null) {
            return Result.error("无法获取用户信息，请重新登录");
        }
        
        Page<ConsumptionRecord> result = consumptionRecordService.getMyRecords(currentUserId, page, size, startDate, endDate, category);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNum", result.getCurrent());
        data.put("pageSize", result.getSize());
        
        return Result.success(data);
    }
}
