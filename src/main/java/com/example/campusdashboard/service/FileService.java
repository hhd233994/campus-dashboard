package com.example.campusdashboard.service;

import com.example.campusdashboard.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @return 文件上传响应
     */
    FileUploadResponse uploadFile(MultipartFile file);
}
