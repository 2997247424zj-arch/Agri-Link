package com.example.agrilinkback.module.notification.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.notification.entity.Notification;
import com.example.agrilinkback.module.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知接口，支持查询、未读计数和已读标记。
 *
 * <p>通过请求头 {@code X-User-Name} 识别当前用户。</p>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    public static final String USER_HEADER = "X-User-Name";

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<Notification>> list(HttpServletRequest request) {
        return ApiResponse.success(notificationService.listByUser(resolveUser(request)));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Integer> unreadCount(HttpServletRequest request) {
        return ApiResponse.success(notificationService.countUnread(resolveUser(request)));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(HttpServletRequest request) {
        notificationService.markAllAsRead(resolveUser(request));
        return ApiResponse.ok();
    }

    private String resolveUser(HttpServletRequest request) {
        String userName = request.getHeader(USER_HEADER);
        return userName != null ? userName : "";
    }
}
