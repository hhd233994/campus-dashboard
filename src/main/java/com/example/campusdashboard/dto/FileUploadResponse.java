package com.example.campusdashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadResponse {
    
    @Schema(description = "文件URL", example = "/uploads/2026/04/25/abc123.jpg")
    private String url;
    
    @Schema(description = "文件名", example = "receipt.jpg")
    private String fileName;
    
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;
    
    @Schema(description = "文件类型", example = "image/jpeg")
    private String contentType;
}
