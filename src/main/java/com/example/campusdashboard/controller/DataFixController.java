package com.example.campusdashboard.controller;

import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.entity.ConsumptionRecord;
import com.example.campusdashboard.service.ConsumptionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据修复控制器 - 用于修复中文乱码问题
 */
@Tag(name = "数据修复", description = "修复中文乱码等数据问题")
@RestController
@RequestMapping("/api/fix")
public class DataFixController {
    
    @Autowired
    private ConsumptionRecordService consumptionRecordService;
    
    /**
     * 重新插入测试数据（修复中文乱码）
     */
    @Operation(summary = "重新插入测试数据", description = "清空旧数据并插入正确的中文测试数据")
    @PostMapping("/reinsert-test-data")
    public Result<String> reinsertTestData() {
        try {
            // 删除所有消费记录（通过分页获取所有ID然后删除）
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<ConsumptionRecord> page = 
                consumptionRecordService.listRecords(1, 1000);
            for (ConsumptionRecord record : page.getRecords()) {
                consumptionRecordService.deleteRecord(record.getId());
            }
            
            // 重新插入测试数据
            List<ConsumptionRecord> testData = new ArrayList<>();
            
            testData.add(createRecord(2L, "食堂", 15.50, "午餐"));
            testData.add(createRecord(2L, "超市", 32.00, "购买生活用品"));
            testData.add(createRecord(2L, "食堂", 12.00, "早餐"));
            testData.add(createRecord(3L, "食堂", 18.00, "晚餐"));
            testData.add(createRecord(3L, "网吧", 10.00, "上网2小时"));
            testData.add(createRecord(2L, "图书馆", 5.00, "打印资料"));
            testData.add(createRecord(2L, "咖啡店", 25.00, "下午茶"));
            testData.add(createRecord(2L, "奶茶店", 18.00, "珍珠奶茶"));
            testData.add(createRecord(3L, "健身房", 200.00, "月卡充值"));
            testData.add(createRecord(2L, "电影院", 60.00, "看电影"));
            testData.add(createRecord(3L, "餐厅", 120.00, "朋友聚餐"));
            
            boolean success = consumptionRecordService.batchAddRecords(testData);
            
            if (success) {
                return Result.success("测试数据重新插入成功，共 " + testData.size() + " 条", null);
            } else {
                return Result.error("数据插入失败");
            }
        } catch (Exception e) {
            return Result.error("数据修复失败: " + e.getMessage());
        }
    }
    
    private ConsumptionRecord createRecord(Long userId, String category, double amount, String description) {
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(userId);
        record.setCategory(category);
        record.setAmount(new BigDecimal(amount));
        record.setDescription(description);
        return record;
    }
}
