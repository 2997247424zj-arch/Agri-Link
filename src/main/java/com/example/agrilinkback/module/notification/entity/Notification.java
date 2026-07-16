package com.example.agrilinkback.module.notification.entity;

import java.time.LocalDateTime;

/**
 * 系统通知实体，对应 tb_notification 表。
 *
 * <p>{@code targetUser} 为 {@code null} 时表示全局广播，所有用户可见。</p>
 */
public record Notification(
        Integer notificationId,
        String targetUser,
        String title,
        String content,
        String type,
        Boolean isRead,
        String creator,
        LocalDateTime createTime
) {
}
