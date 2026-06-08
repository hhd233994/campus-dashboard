package com.example.campusdashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campusdashboard.dto.CategoryStatistics;
import com.example.campusdashboard.dto.DailyStatistics;
import com.example.campusdashboard.entity.ConsumptionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 消费记录Mapper接口
 */
@Mapper
public interface ConsumptionRecordMapper extends BaseMapper<ConsumptionRecord> {
    
    /**
     * 根据userId查询消费记录，按createTime降序排列
     */
    List<ConsumptionRecord> selectByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);
    
    /**
     * 按分类统计消费数据
     */
    List<CategoryStatistics> selectCategoryStatistics();
    
    /**
     * 按日期统计消费数据（最近7天）
     */
    List<DailyStatistics> selectDailyStatistics(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    /**
     * 查询总消费金额
     */
    BigDecimal selectTotalAmount();
    
    /**
     * 查询今日消费金额
     */
    BigDecimal selectTodayAmount(@Param("today") LocalDate today);
    
    /**
     * 查询指定用户的总消费金额
     */
    BigDecimal selectTotalAmountByUserId(@Param("userId") Long userId);
    
    /**
     * 查询指定用户的今日消费金额
     */
    BigDecimal selectTodayAmountByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);
    
    /**
     * 查询指定用户的分类统计
     */
    List<CategoryStatistics> selectCategoryStatisticsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询指定用户的每日统计
     */
    List<DailyStatistics> selectDailyStatisticsByUserId(@Param("userId") Long userId, 
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}
