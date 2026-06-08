package com.example.campusdashboard.controller;

import com.example.campusdashboard.common.Result;
import com.example.campusdashboard.dto.FileUploadResponse;
import com.example.campusdashboard.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Tag(name = "文件管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/api/file")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    /**
     * 上传文件
     */
    @Operation(summary = "上传文件", description = "上传图片、PDF等文件（最大5MB）")
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileService.uploadFile(file);
            return Result.success("上传成功", response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
