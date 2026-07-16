package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 专家答复/处理请求。
 *
 * <p>{@code status} 可省略：未传时由服务层默认置为 1（已处理），
 * 避免仅提交答复正文时因校验失败返回 400。</p>
 */
public record AnswerRequest(
        @NotBlank String answer,
        Integer status
) {
}
