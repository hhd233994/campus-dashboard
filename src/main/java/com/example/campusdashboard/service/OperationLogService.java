package com.example.campusdashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusdashboard.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {
    
    /**
     * 分页查询操作日志
     */
    Page<OperationLog> listLogs(int page, int size, Long userId, String operation, String module);
}
