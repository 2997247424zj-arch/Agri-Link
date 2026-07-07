package com.example.agrilinkback.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        int code,
        String message,
        T data,
        Instant timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "success", data, Instant.now());
    }

    public static ApiResponse<Void> ok() {
        return success(null);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(false, code, message, null, Instant.now());
    }
}
