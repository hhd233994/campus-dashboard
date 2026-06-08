package com.example.campusdashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;
import com.example.campusdashboard.service.ConsumptionRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 消费记录控制器测试类
 */
@WebMvcTest(ConsumptionRecordController.class)
class ConsumptionRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumptionRecordService recordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddRecord_Success() throws Exception {
        // Given
        ConsumptionRecord record = new ConsumptionRecord();
        record.setUserId(1L);
        record.setAmount(new BigDecimal("50.00"));
        record.setCategory("餐饮");
        record.setDescription("午餐");
        record.setCreateTime(LocalDateTime.now());

        when(recordService.addRecord(any(ConsumptionRecord.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/record/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(record)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("添加成功"));

        verify(recordService, times(1)).addRecord(any(ConsumptionRecord.class));
    }

    @Test
    void testListRecords_Success() throws Exception {
        // Given
        Page<ConsumptionRecord> mockPage = new Page<>(1, 10);
        ConsumptionRecord record = new ConsumptionRecord();
        record.setId(1L);
        record.setAmount(new BigDecimal("50.00"));
        record.setCategory("餐饮");
        mockPage.setRecords(Arrays.asList(record));
        mockPage.setTotal(1);

        when(recordService.listRecords(anyInt(), anyInt(), isNull(), isNull(), isNull()))
            .thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/record/list")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1));

        verify(recordService, times(1)).listRecords(1, 10, null, null, null);
    }

    @Test
    void testDeleteRecord_Success() throws Exception {
        // Given
        when(recordService.deleteRecord(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/record/delete/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"));

        verify(recordService, times(1)).deleteRecord(1L);
    }

    @Test
    void testGetRecordById_Success() throws Exception {
        // Given
        ConsumptionRecord record = new ConsumptionRecord();
        record.setId(1L);
        record.setAmount(new BigDecimal("50.00"));
        record.setCategory("餐饮");

        when(recordService.getRecordById(1L)).thenReturn(record);

        // When & Then
        mockMvc.perform(get("/api/record/detail/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.category").value("餐饮"));

        verify(recordService, times(1)).getRecordById(1L);
    }

    @Test
    void testUpdateRecord_Success() throws Exception {
        // Given
        ConsumptionRecord record = new ConsumptionRecord();
        record.setId(1L);
        record.setAmount(new BigDecimal("60.00"));
        record.setCategory("交通");

        when(recordService.updateRecord(any(ConsumptionRecord.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/record/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(record)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"));

        verify(recordService, times(1)).updateRecord(any(ConsumptionRecord.class));
    }

    @Test
    void testGetCategoryStatistics_Success() throws Exception {
        // Given
        CategoryStatistics stats1 = new CategoryStatistics();
        stats1.setCategory("餐饮");
        stats1.setTotalAmount(new BigDecimal("500.00"));
        stats1.setCount(10L);

        List<CategoryStatistics> mockStats = Arrays.asList(stats1);
        when(recordService.getCategoryStatistics()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/record/statistics/category"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].category").value("餐饮"))
            .andExpect(jsonPath("$.data[0].totalAmount").value(500.00));

        verify(recordService, times(1)).getCategoryStatistics();
    }

    @Test
    void testGetDailyStatistics_Success() throws Exception {
        // Given
        DailyStatistics stats = new DailyStatistics();
        stats.setDate(LocalDate.now());
        stats.setTotalAmount(new BigDecimal("200.00"));
        stats.setCount(5L);

        List<DailyStatistics> mockStats = Arrays.asList(stats);
        when(recordService.getDailyStatistics()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/record/statistics/daily"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].totalAmount").value(200.00));

        verify(recordService, times(1)).getDailyStatistics();
    }

    @Test
    void testGetTotalAmount_Success() throws Exception {
        // Given
        when(recordService.getTotalAmount()).thenReturn(new BigDecimal("1000.00"));

        // When & Then
        mockMvc.perform(get("/api/record/statistics/total"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(1000.00));

        verify(recordService, times(1)).getTotalAmount();
    }

    @Test
    void testGetTodayAmount_Success() throws Exception {
        // Given
        when(recordService.getTodayAmount()).thenReturn(new BigDecimal("150.00"));

        // When & Then
        mockMvc.perform(get("/api/record/statistics/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(150.00));

        verify(recordService, times(1)).getTodayAmount();
    }
}
