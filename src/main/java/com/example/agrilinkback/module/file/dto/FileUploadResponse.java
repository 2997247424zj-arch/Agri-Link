package com.example.agrilinkback.module.file.dto;

public record FileUploadResponse(
        String fileName,
        String originalName,
        String contentType,
        long size,
        String url
) {
}
