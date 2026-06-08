package com.example.campusdashboard.service;

import com.example.demo.DemoApplication;
import com.example.campusdashboard.dto.FileUploadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件服务测试类
 */
@SpringBootTest(classes = DemoApplication.class)
class FileServiceImplTest {

    @Autowired
    private FileService fileService;

    @Test
    void testUploadFile_Success() {
        // Given - 创建一个模拟图片文件
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake image content for testing".getBytes()
        );

        // When
        FileUploadResponse result = fileService.uploadFile(mockFile);

        // Then
        assertNotNull(result, "上传结果不应该为空");
        assertNotNull(result.getUrl(), "URL不应该为空");
        assertTrue(result.getFileName().contains("test-image"), "文件名应该包含原始名称");
    }

    @Test
    void testUploadFile_EmptyFile() {
        // Given - 空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            new byte[0]
        );

        // When & Then - 应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            fileService.uploadFile(emptyFile);
        }, "空文件应该抛出异常");
    }

    @Test
    void testUploadFile_UnsupportedType() {
        // Given - 不支持的文件类型
        MockMultipartFile exeFile = new MockMultipartFile(
            "file",
            "malware.exe",
            "application/x-msdownload",
            "fake exe content".getBytes()
        );

        // When & Then - 应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            fileService.uploadFile(exeFile);
        }, "不支持的文件类型应该抛出异常");
    }
}
