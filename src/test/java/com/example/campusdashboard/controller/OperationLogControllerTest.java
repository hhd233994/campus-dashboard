package com.example.campusdashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.entity.OperationLog;
import com.example.campusdashboard.service.OperationLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 操作日志控制器测试类
 */
@WebMvcTest(OperationLogController.class)
class OperationLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationLogService logService;

    @Test
    void testListLogs_Success() throws Exception {
        // Given
        Page<OperationLog> mockPage = new Page<>(1, 10);
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setUsername("admin");
        log.setOperation("ADD");
        log.setModule("消费记录");
        log.setStatusCode(200);
        log.setCreateTime(LocalDateTime.now());
        mockPage.setRecords(Arrays.asList(log));
        mockPage.setTotal(1);

        when(logService.listLogs(anyInt(), anyInt(), isNull(), isNull(), isNull()))
            .thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/log/list")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.records[0].username").value("admin"))
            .andExpect(jsonPath("$.data.records[0].operation").value("ADD"));

        verify(logService, times(1)).listLogs(1, 10, null, null, null);
    }

    @Test
    void testListLogs_WithFilters() throws Exception {
        // Given
        Page<OperationLog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList());
        mockPage.setTotal(0);

        when(logService.listLogs(anyInt(), anyInt(), eq(1L), eq("DELETE"), eq("消费记录")))
            .thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/log/list")
                .param("page", "1")
                .param("size", "10")
                .param("userId", "1")
                .param("operation", "DELETE")
                .param("module", "消费记录"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));

        verify(logService, times(1)).listLogs(1, 10, 1L, "DELETE", "消费记录");
    }
}
