package com.example.campusdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;
import com.example.campusdashboard.mapper.ConsumptionRecordMapper;
import com.example.campusdashboard.service.ConsumptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费记录服务实现类
 */
@Service
public class ConsumptionRecordServiceImpl implements ConsumptionRecordService {
    
    @Autowired
    private ConsumptionRecordMapper consumptionRecordMapper;
    
    @Override
    @CacheEvict(value = "records", allEntries = true)
    public boolean addRecord(ConsumptionRecord record) {
        return consumptionRecordMapper.insert(record) > 0;
    }
    
    @Override
    @Cacheable(value = "records", key = "'page:' + #page + ':size:' + #size")
    public Page<ConsumptionRecord> listRecords(int page, int size) {
        return listRecords(page, size, null, null, null);
    }
    
    @Override
    public Page<ConsumptionRecord> listRecords(int page, int size, String startDate, String endDate, String category) {
        Page<ConsumptionRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<ConsumptionRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 添加日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(ConsumptionRecord::getCreateTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            wrapper.le(ConsumptionRecord::getCreateTime, endDateTime);
        }
        
        // 添加分类筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq(ConsumptionRecord::getCategory, category);
        }
        
        wrapper.orderByDesc(ConsumptionRecord::getCreateTime);
        return consumptionRecordMapper.selectPage(pageInfo, wrapper);
    }
    
    @Override
    @Cacheable(value = "records", key = "'user:' + #userId")
    public List<ConsumptionRecord> getRecordsByUserId(Long userId) {
        return consumptionRecordMapper.selectByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Override
    @CacheEvict(value = "records", allEntries = true)
    public boolean deleteRecord(Long id) {
        return consumptionRecordMapper.deleteById(id) > 0;
    }
    
    @Override
    @Cacheable(value = "records", key = "'id:' + #id")
    public ConsumptionRecord getRecordById(Long id) {
        return consumptionRecordMapper.selectById(id);
    }
    
    @Override
    @CacheEvict(value = "records", allEntries = true)
    public boolean updateRecord(ConsumptionRecord record) {
        return consumptionRecordMapper.updateById(record) > 0;
    }
    
    @Override
    @Cacheable(value = "statistics", key = "'category'")
    public List<CategoryStatistics> getCategoryStatistics() {
        return consumptionRecordMapper.selectCategoryStatistics();
    }
    
    @Override
    @Cacheable(value = "statistics", key = "'daily:' + T(java.time.LocalDate).now()")
    public List<DailyStatistics> getDailyStatistics() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 最近7天
        return consumptionRecordMapper.selectDailyStatistics(startDate, endDate);
    }
    
    @Override
    public BigDecimal getTotalAmount() {
        return consumptionRecordMapper.selectTotalAmount();
    }
    
    @Override
    public BigDecimal getTodayAmount() {
        return consumptionRecordMapper.selectTodayAmount(LocalDate.now());
    }
    
    @Override
    @Cacheable(value = "statistics", key = "'overview'")
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalAmount", getTotalAmount());
        overview.put("todayAmount", getTodayAmount());
        
        // 获取总消费次数
        LambdaQueryWrapper<ConsumptionRecord> wrapper = new LambdaQueryWrapper<>();
        Long totalCount = consumptionRecordMapper.selectCount(wrapper);
        overview.put("totalCount", totalCount);
        
        overview.put("categoryStats", getCategoryStatistics());
        overview.put("dailyStats", getDailyStatistics());
        return overview;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "records", allEntries = true)
    public boolean batchAddRecords(List<ConsumptionRecord> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        
        // 批量插入，任何一条失败都会回滚
        for (ConsumptionRecord record : records) {
            int result = consumptionRecordMapper.insert(record);
            if (result <= 0) {
                throw new RuntimeException("批量添加失败，已回滚");
            }
        }
        return true;
    }
    
    @Override
    public Map<String, Object> getUserOverview(Long userId) {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalAmount", consumptionRecordMapper.selectTotalAmountByUserId(userId));
        overview.put("todayAmount", consumptionRecordMapper.selectTodayAmountByUserId(userId, LocalDate.now()));
        
        // 获取用户总消费次数
        LambdaQueryWrapper<ConsumptionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumptionRecord::getUserId, userId);
        Long totalCount = consumptionRecordMapper.selectCount(wrapper);
        overview.put("totalCount", totalCount);
        
        overview.put("categoryStats", consumptionRecordMapper.selectCategoryStatisticsByUserId(userId));
        overview.put("dailyStats", consumptionRecordMapper.selectDailyStatisticsByUserId(userId, LocalDate.now().minusDays(6), LocalDate.now()));
        return overview;
    }
    
    @Override
    public Page<ConsumptionRecord> getMyRecords(Long userId, int page, int size, String startDate, String endDate, String category) {
        Page<ConsumptionRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<ConsumptionRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 必须指定用户ID
        wrapper.eq(ConsumptionRecord::getUserId, userId);
        
        // 添加日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(ConsumptionRecord::getCreateTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            wrapper.le(ConsumptionRecord::getCreateTime, endDateTime);
        }
        
        // 添加分类筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq(ConsumptionRecord::getCategory, category);
        }
        
        wrapper.orderByDesc(ConsumptionRecord::getCreateTime);
        return consumptionRecordMapper.selectPage(pageInfo, wrapper);
    }
}
