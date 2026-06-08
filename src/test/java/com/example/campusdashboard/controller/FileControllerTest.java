package com.example.campusdashboard.controller;

import com.example.campusdashboard.dto.FileUploadResponse;
import com.example.campusdashboard.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文件上传控制器测试类
 */
@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void testUploadFile_Success() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake image content".getBytes()
        );

        FileUploadResponse mockResponse = new FileUploadResponse();
        mockResponse.setUrl("/uploads/test.jpg");
        mockResponse.setFileName("test.jpg");
        mockResponse.setFileSize(1024L);
        mockResponse.setContentType("image/jpeg");
        
        when(fileService.uploadFile(any())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(multipart("/api/file/upload")
                .file(mockFile))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("上传成功"));

        verify(fileService, times(1)).uploadFile(any());
    }

    @Test
    void testUploadFile_Failure() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake image content".getBytes()
        );

        when(fileService.uploadFile(any())).thenThrow(new RuntimeException("上传失败"));

        // When & Then
        mockMvc.perform(multipart("/api/file/upload")
                .file(mockFile))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(500));

        verify(fileService, times(1)).uploadFile(any());
    }
}
