package com.example.campusdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "每日统计数据")
public class DailyStatistics {
    
    @Schema(description = "日期", example = "2026-04-25")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;
    
    @Schema(description = "消费次数", example = "5")
    private Long count;
    
    @Schema(description = "总金额", example = "75.50")
    private BigDecimal totalAmount;
}
