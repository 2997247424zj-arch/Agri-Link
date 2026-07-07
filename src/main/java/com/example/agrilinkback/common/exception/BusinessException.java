package com.example.agrilinkback.common.exception;

/**
 * 业务异常统一携带业务 code，交由 GlobalExceptionHandler 转换成 ApiResponse。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
