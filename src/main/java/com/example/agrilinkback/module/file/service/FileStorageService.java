package com.example.agrilinkback.module.file.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.file.dto.FileUploadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本地文件存储服务。
 *
 * <p>当前只开放图片上传，返回的 /files/** URL 由 WebConfig 映射到本地上传目录。
 */
@Service
public class FileStorageService {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif",
            "image/webp", "webp"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${application.upload-path}") String uploadPath) {
        this.uploadRoot = Path.of(uploadPath).toAbsolutePath().normalize();
    }

    public FileUploadResponse storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is required");
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String extension = resolveExtension(originalName, contentType);
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Only jpg, png, gif and webp images are supported");
        }
        if (!contentType.isBlank() && !contentType.startsWith("image/")) {
            throw new BusinessException("Uploaded file must be an image");
        }

        String fileName = UUID.randomUUID() + "." + extension;
        Path target = uploadRoot.resolve(fileName).normalize();
        if (!target.startsWith(uploadRoot)) {
            // 防止构造异常路径逃逸上传目录。
            throw new BusinessException("Invalid upload path");
        }

        try {
            Files.createDirectories(uploadRoot);
            file.transferTo(target);
        } catch (IOException ex) {
            throw new BusinessException(500, "Failed to store uploaded image");
        }

        return new FileUploadResponse(
                fileName,
                originalName,
                contentType,
                file.getSize(),
                "/files/" + fileName
        );
    }

    private String resolveExtension(String originalName, String contentType) {
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < originalName.length() - 1) {
            return originalName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
        }
        return EXTENSION_BY_CONTENT_TYPE.getOrDefault(contentType, "");
    }
}
