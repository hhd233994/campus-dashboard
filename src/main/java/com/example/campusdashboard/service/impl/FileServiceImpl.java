package com.example.campusdashboard.service.impl;

import com.example.campusdashboard.dto.FileUploadResponse;
import com.example.campusdashboard.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    // 允许的文件类型
    private static final String[] ALLOWED_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf",
        "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
    
    // 最大文件大小：5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        // 验证文件
        validateFile(file);
        
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 按日期组织目录结构：uploads/2026/04/25/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String dirPath = uploadPath + datePath;
            
            // 创建目录
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = dirPath + "/" + fileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            log.info("文件上传成功: {}", filePath);
            
            // 构建响应
            FileUploadResponse response = new FileUploadResponse();
            response.setUrl("/uploads/" + datePath + "/" + fileName);
            response.setFileName(originalFilename);
            response.setFileSize(file.getSize());
            response.setContentType(file.getContentType());
            
            return response;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("无法识别文件类型");
        }
        
        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                allowed = true;
                break;
            }
        }
        
        if (!allowed) {
            throw new RuntimeException("不支持的文件类型: " + contentType);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
