package com.example.campusdashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分类统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分类统计数据")
public class CategoryStatistics {
    
    @Schema(description = "消费类别", example = "食堂")
    private String category;
    
    @Schema(description = "消费次数", example = "10")
    private Long count;
    
    @Schema(description = "总金额", example = "150.50")
    private BigDecimal totalAmount;
    
    @Schema(description = "平均金额", example = "15.05")
    private BigDecimal avgAmount;
}
