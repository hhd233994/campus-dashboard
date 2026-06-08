package com.example.campusdashboard.service;

import com.example.demo.DemoApplication;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消费记录服务测试类
 */
@SpringBootTest(classes = DemoApplication.class)
class ConsumptionRecordServiceTest {
    
    @Autowired
    private ConsumptionRecordService consumptionRecordService;
    
    @Test
    void testAddRecord() {
        // 准备测试数据
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(1L);
        record.setCategory("食堂");
        record.setAmount(new BigDecimal("20.50"));
        record.setDescription("测试午餐");
        record.setCreateTime(LocalDateTime.now());
        
        // 执行测试
        boolean result = consumptionRecordService.addRecord(record);
        
        // 验证结果
        assertTrue(result, "添加记录应该成功");
        assertNotNull(record.getId(), "记录ID不应该为空");
    }
    
    @Test
    void testListRecords() {
        // 执行测试
        Page<ConsumptionRecord> page = consumptionRecordService.listRecords(1, 10);
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        assertTrue(page.getTotal() >= 0, "总记录数应该大于等于0");
        assertNotNull(page.getRecords(), "记录列表不应该为空");
    }
    
    @Test
    void testGetRecordsByUserId() {
        // 执行测试
        List<ConsumptionRecord> records = consumptionRecordService.getRecordsByUserId(1L);
        
        // 验证结果
        assertNotNull(records, "记录列表不应该为空");
    }
    
    @Test
    void testGetRecordById() {
        // 先添加一条记录
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(1L);
        record.setCategory("超市");
        record.setAmount(new BigDecimal("50.00"));
        record.setDescription("测试商品");
        record.setCreateTime(LocalDateTime.now());
        consumptionRecordService.addRecord(record);
        
        // 执行测试
        ConsumptionRecord found = consumptionRecordService.getRecordById(record.getId());
        
        // 验证结果
        assertNotNull(found, "查询到的记录不应该为空");
        assertEquals(record.getUserId(), found.getUserId(), "用户ID应该一致");
        assertEquals(record.getCategory(), found.getCategory(), "消费类别应该一致");
    }
    
    @Test
    void testDeleteRecord() {
        // 先添加一条记录
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(1L);
        record.setCategory("网吧");
        record.setAmount(new BigDecimal("10.00"));
        record.setDescription("测试上网");
        record.setCreateTime(LocalDateTime.now());
        consumptionRecordService.addRecord(record);
        
        Long recordId = record.getId();
        
        // 执行删除
        boolean result = consumptionRecordService.deleteRecord(recordId);
        
        // 验证结果
        assertTrue(result, "删除记录应该成功");
        
        // 验证记录已被删除
        ConsumptionRecord deleted = consumptionRecordService.getRecordById(recordId);
        assertNull(deleted, "删除后的记录应该查询不到");
    }
    
    @Test
    void testGetCategoryStatistics() {
        // 执行测试
        List<CategoryStatistics> statistics = consumptionRecordService.getCategoryStatistics();
        
        // 验证结果
        assertNotNull(statistics, "分类统计不应该为空");
        assertFalse(statistics.isEmpty(), "分类统计应该有数据");
        
        // 验证每个统计数据
        for (CategoryStatistics stat : statistics) {
            assertNotNull(stat.getCategory(), "类别不应该为空");
            assertTrue(stat.getCount() > 0, "消费次数应该大于0");
            assertTrue(stat.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, "总金额应该大于0");
        }
    }
    
    @Test
    void testGetDailyStatistics() {
        // 执行测试
        List<DailyStatistics> statistics = consumptionRecordService.getDailyStatistics();
        
        // 验证结果
        assertNotNull(statistics, "每日统计不应该为空");
        
        // 验证每个统计数据
        for (DailyStatistics stat : statistics) {
            assertNotNull(stat.getDate(), "日期不应该为空");
            assertTrue(stat.getCount() >= 0, "消费次数应该大于等于0");
            assertTrue(stat.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0, "总金额应该大于等于0");
        }
    }
    
    @Test
    void testGetTotalAmount() {
        // 执行测试
        BigDecimal totalAmount = consumptionRecordService.getTotalAmount();
        
        // 验证结果
        assertNotNull(totalAmount, "总金额不应该为空");
        assertTrue(totalAmount.compareTo(BigDecimal.ZERO) >= 0, "总金额应该大于等于0");
    }
    
    @Test
    void testGetTodayAmount() {
        // 执行测试
        BigDecimal todayAmount = consumptionRecordService.getTodayAmount();
        
        // 验证结果
        assertNotNull(todayAmount, "今日金额不应该为空");
        assertTrue(todayAmount.compareTo(BigDecimal.ZERO) >= 0, "今日金额应该大于等于0");
    }
    
    @Test
    void testListRecordsWithFilter() {
        // 执行测试 - 带筛选条件
        Page<ConsumptionRecord> page = consumptionRecordService.listRecords(
            1, 10, "2024-01-01", "2026-12-31", "食堂"
        );
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        assertTrue(page.getTotal() >= 0, "总记录数应该大于等于0");
        
        // 验证筛选结果
        for (ConsumptionRecord record : page.getRecords()) {
            assertEquals("食堂", record.getCategory(), "筛选后的记录类别应该是食堂");
        }
    }
    
    @Test
    @Transactional
    void testBatchAddRecords() {
        // 准备测试数据
        List<ConsumptionRecord> records = List.of(
            createTestRecord(1L, "食堂", new BigDecimal("15.00")),
            createTestRecord(1L, "超市", new BigDecimal("30.00")),
            createTestRecord(1L, "网吧", new BigDecimal("10.00"))
        );
        
        // 执行测试
        boolean result = consumptionRecordService.batchAddRecords(records);
        
        // 验证结果
        assertTrue(result, "批量添加应该成功");
    }
    
    @Test
    @Transactional
    void testUpdateRecord() {
        // 先创建一个记录
        ConsumptionRecord record = createTestRecord(1L, "食堂", new BigDecimal("20.00"));
        consumptionRecordService.addRecord(record);
        
        // 修改记录
        assertNotNull(record.getId(), "记录ID不应该为空");
        record.setAmount(new BigDecimal("25.00"));
        record.setCategory("超市");
        record.setDescription("更新后的描述");
        
        // 执行更新
        boolean result = consumptionRecordService.updateRecord(record);
        
        // 验证结果
        assertTrue(result, "更新应该成功");
        
        // 验证更新后的数据
        ConsumptionRecord updated = consumptionRecordService.getRecordById(record.getId());
        assertNotNull(updated, "更新后的记录不应该为空");
        assertEquals(new BigDecimal("25.00"), updated.getAmount(), "金额应该已更新");
        assertEquals("超市", updated.getCategory(), "类别应该已更新");
    }
    
    @Test
    void testGetOverview() {
        // 执行测试 - 管理员查看总览
        Map<String, Object> overview = consumptionRecordService.getOverview();
        
        // 验证结果
        assertNotNull(overview, "总览数据不应该为空");
        assertTrue(overview.containsKey("totalAmount"), "应该包含总金额");
        assertTrue(overview.containsKey("todayAmount"), "应该包含今日金额");
        assertTrue(overview.containsKey("recordCount"), "应该包含记录数");
    }
    
    @Test
    void testGetUserOverview() {
        // 执行测试 - 用户查看个人总览
        Map<String, Object> overview = consumptionRecordService.getUserOverview(1L);
        
        // 验证结果
        assertNotNull(overview, "用户总览数据不应该为空");
        assertTrue(overview.containsKey("totalAmount"), "应该包含总金额");
        assertTrue(overview.containsKey("todayAmount"), "应该包含今日金额");
    }
    
    @Test
    void testGetMyRecords() {
        // 执行测试 - 获取当前用户的记录
        Page<ConsumptionRecord> page = consumptionRecordService.getMyRecords(
            1L, 1, 10, null, null, null
        );
        
        // 验证结果
        assertNotNull(page, "个人记录分页不应该为空");
        assertTrue(page.getTotal() >= 0, "总记录数应该大于等于0");
        
        // 验证所有记录都属于该用户
        for (ConsumptionRecord record : page.getRecords()) {
            assertEquals(1L, record.getUserId(), "记录应该属于当前用户");
        }
    }
    
    private ConsumptionRecord createTestRecord(Long userId, String category, BigDecimal amount) {
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(userId);
        record.setCategory(category);
        record.setAmount(amount);
        record.setDescription("测试记录");
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
}
