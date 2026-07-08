package com.example.agrilinkback.common.exception;

import com.example.agrilinkback.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理器，通过 {@code @RestControllerAdvice} 拦截控制器层向上
 * 抛出的所有异常，确保每一个 HTTP 响应（包括错误响应）都保持统一的
 * {@link ApiResponse} JSON 结构，便于前端一致地解析和处理。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>将业务异常（{@link BusinessException}）映射为对应的 HTTP 状态码和
 *       统一响应体。</li>
 *   <li>将 Spring MVC 参数校验异常
 *       （{@link MethodArgumentNotValidException} / {@link BindException}）
 *       转换为可读的错误消息。</li>
 *   <li>处理请求体格式错误
 *       （{@link HttpMessageNotReadableException}）。</li>
 *   <li>兜底捕获所有未预期的 {@link Exception}，记录日志但不向前端暴露
 *       内部堆栈细节，仅返回通用的 500 错误。</li>
 * </ul>
 *
 * <p>所有响应均通过 {@link ApiResponse#fail(int, String)} 构造失败结果，
 * 保证与成功响应使用相同的 JSON 外骨架。</p>
 *
 * @author AgriLink Team
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 日志记录器，用于记录未预期异常的堆栈信息。 */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 {@link BusinessException} 类型的业务异常。
     *
     * <p>从异常中取出业务状态码（如 400、404、409 等），尝试解析为对应的
     * HTTP 状态码；若无法解析则默认使用 {@link HttpStatus#BAD_REQUEST}（400），
     * 最后以该状态码和异常消息构造统一的失败响应体。</p>
     *
     * @param ex 业务异常实例，携带业务状态码和面向用户的错误消息
     * @return 包含 HTTP 状态码和 {@link ApiResponse} 失败结果的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        // 尝试将业务异常中的整型状态码映射为 Spring HTTP 状态枚举
        HttpStatus status = HttpStatus.resolve(ex.getCode());
        if (status == null) {
            // 无法映射的状态码统一回退为 400 BAD_REQUEST
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理 {@link MethodArgumentNotValidException} 异常。
     *
     * <p>该异常由 {@code @Valid} / {@code @Validated} 注解在控制器方法参数上
     * 触发（通常用于 {@code @RequestBody} 的 DTO 校验）。将多个字段的校验失败
     * 信息合并为一条可读的字符串，以 400 状态码返回。</p>
     *
     * @param ex 方法参数校验异常，包含所有字段错误的绑定结果
     * @return HTTP 400 响应，消息体包含各字段校验失败的描述
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return badRequest(validationMessage(ex));
    }

    /**
     * 处理 {@link BindException} 异常。
     *
     * <p>该异常通常在使用简单类型参数绑定时触发（如 URL 查询参数、表单数据
     * 绑定到 POJO 时校验失败）。处理方式与
     * {@link #handleMethodArgumentNotValid(MethodArgumentNotValidException)}
     * 一致 —— 合并字段错误并以 400 返回。</p>
     *
     * @param ex 数据绑定异常，包含绑定结果中所有字段错误
     * @return HTTP 400 响应，消息体包含各字段校验失败的描述
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        return badRequest(validationMessage(ex));
    }

    /**
     * 处理 {@link HttpMessageNotReadableException} 异常。
     *
     * <p>该异常在请求体无法被正确解析时抛出（例如 JSON 格式错误、数据类型
     * 不匹配等）。直接返回固定的 "Request body is invalid" 错误消息，不暴露
     * 底层反序列化细节。</p>
     *
     * @return HTTP 400 响应，消息体为固定提示 "Request body is invalid"
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable() {
        return badRequest("Request body is invalid");
    }

    /**
     * 兜底捕获所有未被上述处理器覆盖的异常。
     *
     * <p>这是整个处理器链条的最后一道防线。未预期的异常会在此被捕获，
     * 通过 SLF4J 记录完整的堆栈信息（包括请求 URI 便于溯源），但仅向前端
     * 返回一个通用的 "Internal server error" 消息，绝不暴露内部堆栈细节。</p>
     *
     * @param ex      未被上游处理器捕获的原始异常
     * @param request 当前 HTTP 请求对象，用于日志中记录请求 URI
     * @return HTTP 500 响应，消息体为通用错误提示 "Internal server error"
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex, HttpServletRequest request) {
        // 记录完整堆栈供运维排查，但不向前端暴露细节
        log.error("Unhandled exception on {}", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(500, "Internal server error"));
    }

    /**
     * 快捷构造一个 HTTP 400 BAD_REQUEST 响应。
     *
     * <p>所有参数校验类异常最终都调用此方法，保证 400 响应的状态码和
     * {@link ApiResponse} 结构完全一致。</p>
     *
     * @param message 面向用户的错误消息字符串
     * @return HTTP 400 响应，消息体为 {@link ApiResponse#fail(int, String)} 格式
     */
    private ResponseEntity<ApiResponse<Void>> badRequest(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(400, message));
    }

    /**
     * 从 {@link BindException}（及其子类 {@link MethodArgumentNotValidException}）
     * 的绑定结果中提取所有字段校验错误，并合并为一条以分号分隔的可读消息。
     *
     * <p>格式示例：{@code "username must not be blank; email format is invalid"}。
     * 如果提取后的消息为空字符串，则返回默认的
     * "Request parameter is invalid" 作为兜底提示。</p>
     *
     * @param ex 数据绑定异常，包含 {@code BindingResult} 中的字段错误列表
     * @return 合并后的校验失败描述字符串，永不返回空白或 {@code null}
     */
    private String validationMessage(BindException ex) {
        // 将所有字段错误按 "字段名 错误描述" 格式拼接，分号分隔
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        // 极端情况下流可能为空，此时返回默认提示
        return message.isBlank() ? "Request parameter is invalid" : message;
    }
}