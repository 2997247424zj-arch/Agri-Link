package com.example.agrilinkback.common.api;

import java.time.Instant;

/**
 * 全局统一响应体。
 *
 * <p>所有 Controller 都通过该结构返回结果，前端只需要固定读取 success/code/message/data。
 */
public record ApiResponse<T>(
        boolean success,
        int code,
        String message,
        T data,
        Instant timestamp
) {

    /** 成功响应统一使用 HTTP 200 对应的业务 code。 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "success", data, Instant.now());
    }

    public static ApiResponse<Void> ok() {
        return success(null);
    }

    /** 业务异常、参数异常和权限异常最终都会转换为该失败响应格式。 */
    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(false, code, message, null, Instant.now());
    }
}
