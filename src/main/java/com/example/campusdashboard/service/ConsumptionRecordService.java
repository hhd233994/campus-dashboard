package com.example.campusdashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 消费记录服务接口
 */
public interface ConsumptionRecordService {
    
    /**
     * 新增消费记录
     */
    boolean addRecord(ConsumptionRecord record);
    
    /**
     * 分页查询所有记录
     */
    Page<ConsumptionRecord> listRecords(int page, int size);
    
    /**
     * 分页查询所有记录（带筛选）
     */
    Page<ConsumptionRecord> listRecords(int page, int size, String startDate, String endDate, String category);
    
    /**
     * 根据用户ID查询记录
     */
    List<ConsumptionRecord> getRecordsByUserId(Long userId);
    
    /**
     * 删除记录
     */
    boolean deleteRecord(Long id);
    
    /**
     * 根据ID查询记录
     */
    ConsumptionRecord getRecordById(Long id);
    
    /**
     * 更新记录
     */
    boolean updateRecord(ConsumptionRecord record);
    
    /**
     * 按分类统计消费数据
     */
    List<CategoryStatistics> getCategoryStatistics();
    
    /**
     * 按日期统计消费数据（最近7天）
     */
    List<DailyStatistics> getDailyStatistics();
    
    /**
     * 获取总消费金额
     */
    BigDecimal getTotalAmount();
    
    /**
     * 获取今日消费金额
     */
    BigDecimal getTodayAmount();
    
    /**
     * 获取概览数据
     */
    Map<String, Object> getOverview();
    
    /**
     * 获取指定用户的概览数据
     */
    Map<String, Object> getUserOverview(Long userId);
    
    /**
     * 批量添加消费记录（事务示例）
     */
    boolean batchAddRecords(java.util.List<ConsumptionRecord> records);
    
    /**
     * 查询指定用户的消费记录（分页，支持筛选）
     */
    Page<ConsumptionRecord> getMyRecords(Long userId, int page, int size, String startDate, String endDate, String category);
}
