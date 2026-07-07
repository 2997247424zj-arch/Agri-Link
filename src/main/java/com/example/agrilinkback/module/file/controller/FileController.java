package com.example.agrilinkback.module.file.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.file.dto.FileUploadResponse;
import com.example.agrilinkback.module.file.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口，当前只开放图片上传。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(fileStorageService.storeImage(file));
    }
}
