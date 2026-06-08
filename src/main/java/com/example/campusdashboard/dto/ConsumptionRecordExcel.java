package com.example.campusdashboard.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费记录Excel导出DTO
 */
@Data
public class ConsumptionRecordExcel {
    
    @ExcelProperty(value = "记录ID", index = 0)
    private Long id;
    
    @ExcelProperty(value = "用户ID", index = 1)
    private Long userId;
    
    @ExcelProperty(value = "消费类别", index = 2)
    private String category;
    
    @ExcelProperty(value = "消费金额", index = 3)
    private BigDecimal amount;
    
    @ExcelProperty(value = "备注说明", index = 4)
    private String description;
    
    @ExcelProperty(value = "创建时间", index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
