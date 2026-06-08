package com.example.campusdashboard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费记录实体类
 */
@Data
@Schema(description = "消费记录")
public class ConsumptionRecord {
    
    @Schema(description = "记录ID", example = "1")
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @NotBlank(message = "消费类别不能为空")
    @Schema(description = "消费类别", example = "食堂")
    private String category;
    
    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.01", message = "消费金额必须大于0")
    @Schema(description = "消费金额", example = "15.50")
    private BigDecimal amount;
    
    @Schema(description = "备注说明", example = "午餐")
    private String description;
    
    @Schema(description = "创建时间", example = "2026-04-25T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
