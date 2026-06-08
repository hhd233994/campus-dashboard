package com.example.campusdashboard.service;

import com.example.demo.DemoApplication;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.entity.OperationLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 操作日志服务测试类
 */
@SpringBootTest(classes = DemoApplication.class)
class OperationLogServiceTest {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Test
    void testListLogs() {
        // 执行测试 - 查询所有日志
        Page<OperationLog> page = operationLogService.listLogs(1, 10, null, null, null);
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        assertTrue(page.getTotal() >= 0, "总记录数应该大于等于0");
    }
    
    @Test
    void testListLogsWithUserIdFilter() {
        // 执行测试 - 按用户ID筛选
        Page<OperationLog> page = operationLogService.listLogs(1, 10, 1L, null, null);
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        
        // 验证筛选结果
        for (OperationLog log : page.getRecords()) {
            assertEquals(1L, log.getUserId(), "筛选后的日志用户ID应该是1");
        }
    }
    
    @Test
    void testListLogsWithOperationFilter() {
        // 执行测试 - 按操作类型筛选
        Page<OperationLog> page = operationLogService.listLogs(1, 10, null, "ADD", null);
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        
        // 验证筛选结果
        for (OperationLog log : page.getRecords()) {
            assertEquals("ADD", log.getOperation(), "筛选后的日志操作类型应该是ADD");
        }
    }
    
    @Test
    void testListLogsWithModuleFilter() {
        // 执行测试 - 按模块筛选
        Page<OperationLog> page = operationLogService.listLogs(1, 10, null, null, "消费记录");
        
        // 验证结果
        assertNotNull(page, "分页结果不应该为空");
        
        // 验证筛选结果
        for (OperationLog log : page.getRecords()) {
            assertEquals("消费记录", log.getModule(), "筛选后的日志模块应该是消费记录");
        }
    }
}
