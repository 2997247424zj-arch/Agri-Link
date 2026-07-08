package com.example.agrilinkback.common.api;

import java.time.Instant;

/**
 * 全局统一 API 响应体，封装所有后端返回给前端的 HTTP 响应数据。
 *
 * <h2>设计目的</h2>
 * <ul>
 *   <li><b>统一响应格式</b>：前端只需解析固定的 {@code success/code/message/data/timestamp} 字段，
 *       无需针对不同接口做差异化处理，降低联调成本。</li>
 *   <li><b>语义化成功/失败</b>：通过 {@code success} 布尔字段让前端快速判断业务结果，
 *       配合 {@code code} 业务状态码实现精细化错误处理（如区分"参数错误"与"权限不足"）。</li>
 *   <li><b>类型安全</b>：使用 Java {@code record} 实现不可变数据载体，配合泛型 {@code T} 保证
 *       不同接口返回的 {@code data} 字段在编译期即可确定类型，减少运行时类型转换错误。</li>
 * </ul>
 *
 * <h2>使用约定</h2>
 * <ul>
 *   <li>所有 Controller 层方法必须返回 {@code ApiResponse} 或其子类型，不得直接返回裸数据对象。</li>
 *   <li>成功场景调用静态工厂方法 {@link #success(Object)}，失败场景调用 {@link #fail(int, String)}。</li>
 *   <li>全局异常处理器（如 {@code @ControllerAdvice}）捕获异常后统一转换为 {@code ApiResponse.fail()} 返回。</li>
 *   <li>{@code timestamp} 字段自动填充为当前 UTC 时间，用于前端展示响应时间或排查时序问题。</li>
 * </ul>
 *
 * <h2>字段说明</h2>
 * <table>
 *   <tr><th>字段</th><th>类型</th><th>含义</th></tr>
 *   <tr><td>{@code success}</td><td>{@code boolean}</td><td>业务是否成功；{@code true} 表示正常完成，{@code false} 表示出现异常</td></tr>
 *   <tr><td>{@code code}</td><td>{@code int}</td><td>业务状态码；成功固定为 200，失败时为具体错误码（如 400/403/500 等）</td></tr>
 *   <tr><td>{@code message}</td><td>{@code String}</td><td>人类可读的提示信息；成功为 "success"，失败时为具体错误描述</td></tr>
 *   <tr><td>{@code data}</td><td>{@code T}</td><td>实际的业务数据载荷；成功时携带返回数据，失败时为 {@code null}</td></tr>
 *   <tr><td>{@code timestamp}</td><td>{@code Instant}</td><td>响应生成时刻的 UTC 时间戳，便于日志追踪和调试</td></tr>
 * </table>
 *
 * @param <T> 业务数据的具体类型，由各 Controller 接口按需指定
 * @param success  业务操作是否成功
 * @param code     业务状态码，遵循 HTTP 状态码语义
 * @param message  面向用户的提示信息
 * @param data     成功时携带的业务数据，失败时为 {@code null}
 * @param timestamp 响应生成时的 UTC 时间戳
 *
 * @author AgriLink Team
 * @since 1.0
 */
public record ApiResponse<T>(
        boolean success,
        int code,
        String message,
        T data,
        Instant timestamp
) {

    /**
     * 构建成功的 API 响应。
     *
     * <p>固定使用 HTTP 200 作为业务状态码，消息为 "success"，
     * 时间戳自动取当前 UTC 时刻。这是所有正常业务流程的统一出口。</p>
     *
     * <h3>典型用法</h3>
     * <pre>{@code
     *   // 返回单个对象
     *   return ApiResponse.success(product);
     *
     *   // 返回分页结果
     *   return ApiResponse.success(pageResult);
     *
     *   // 返回列表
     *   return ApiResponse.success(productList);
     * }</pre>
     *
     * @param <T>  业务数据的类型，由传入的 {@code data} 参数自动推断
     * @param data 成功返回的业务数据，可以是实体对象、集合、分页对象或 {@code null}
     * @return 一个标记为成功、code=200、message="success" 的响应体，其 {@code data} 字段携带传入的业务数据
     */
    public static <T> ApiResponse<T> success(T data) {
        // 成功固定 code=200，message="success"，timestamp 取当前 UTC 时间确保一致性
        return new ApiResponse<>(true, 200, "success", data, Instant.now());
    }

    /**
     * 构建"操作成功但无需返回数据"的 API 响应。
     *
     * <p>适用于新增、删除、更新等仅需告知操作结果的场景，内部复用 {@link #success(Object)}
     * 并传入 {@code null} 作为数据载荷。返回类型固定为 {@code ApiResponse<Void>}，
     * 明确表达"该接口无业务数据返回"的语义。</p>
     *
     * <h3>典型用法</h3>
     * <pre>{@code
     *   // 删除操作
     *   @DeleteMapping("/{id}")
     *   public ApiResponse<Void> delete(@PathVariable Long id) {
     *       service.deleteById(id);
     *       return ApiResponse.ok();
     *   }
     *
     *   // 更新操作
     *   @PutMapping("/{id}")
     *   public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Dto dto) {
     *       service.update(id, dto);
     *       return ApiResponse.ok();
     *   }
     * }</pre>
     *
     * @return 一个标记为成功、code=200、message="success" 但 data 为 {@code null} 的响应体
     */
    public static ApiResponse<Void> ok() {
        // 委托给 success()，传入 null 表示无数据返回
        return success(null);
    }

    /**
     * 构建失败的 API 响应。
     *
     * <p>业务异常、参数校验异常、权限异常等所有非正常流程最终都通过此方法转换为统一格式。
     * 全局异常处理器（{@code @RestControllerAdvice}）捕获到各类异常后，提取对应的
     * HTTP 状态码和错误描述，调用本方法构造响应返回给前端。</p>
     *
     * <h3>典型用法</h3>
     * <pre>{@code
     *   // 参数校验失败
     *   return ApiResponse.fail(400, "商品名称不能为空");
     *
     *   // 权限不足
     *   return ApiResponse.fail(403, "无权限执行此操作");
     *
     *   // 资源不存在
     *   return ApiResponse.fail(404, "指定商品不存在");
     *
     *   // 服务器内部错误
     *   return ApiResponse.fail(500, "系统内部异常，请联系管理员");
     * }</pre>
     *
     * <h3>异常处理器集成示例</h3>
     * <pre>{@code
     *   @ExceptionHandler(MethodArgumentNotValidException.class)
     *   public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
     *       String msg = ex.getBindingResult().getFieldError().getDefaultMessage();
     *       return ApiResponse.fail(400, msg);
     *   }
     * }</pre>
     *
     * @param code    业务错误码，遵循 HTTP 状态码语义（400 参数错误、403 权限不足、404 未找到、500 服务端错误等）
     * @param message 面向用户或前端的错误描述信息，应简明扼要地说明失败原因
     * @return 一个标记为失败、携带指定 code 和 message 的响应体，其 {@code data} 始终为 {@code null}
     */
    public static ApiResponse<Void> fail(int code, String message) {
        // 失败场景 data 固定为 null，前端通过 success=false 判断即可
        return new ApiResponse<>(false, code, message, null, Instant.now());
    }
}