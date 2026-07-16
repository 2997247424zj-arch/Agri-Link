package com.example.agrilinkback.module.notification.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.common.security.SecurityUtils;
import com.example.agrilinkback.module.notification.entity.Notification;
import com.example.agrilinkback.module.notification.service.NotificationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知接口，支持查询、未读计数和已读标记。
 *
 * <p>当前用户从 JWT 认证上下文读取，不再信任可伪造的请求头。</p>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<Notification>> list() {
        return ApiResponse.success(notificationService.listByUser(SecurityUtils.currentUserName()));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Integer> unreadCount() {
        return ApiResponse.success(notificationService.countUnread(SecurityUtils.currentUserName()));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead(SecurityUtils.currentUserName());
        return ApiResponse.ok();
    }
}
