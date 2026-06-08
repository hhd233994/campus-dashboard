package com.example.campusdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.entity.OperationLog;
import com.example.campusdashboard.mapper.OperationLogMapper;
import com.example.campusdashboard.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
    @Override
    public Page<OperationLog> listLogs(int page, int size, Long userId, String operation, String module) {
        Page<OperationLog> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 添加用户ID筛选
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        
        // 添加操作类型筛选
        if (operation != null && !operation.isEmpty()) {
            wrapper.eq(OperationLog::getOperation, operation);
        }
        
        // 添加模块筛选
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        
        // 按时间降序排列
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        return operationLogMapper.selectPage(pageInfo, wrapper);
    }
}
