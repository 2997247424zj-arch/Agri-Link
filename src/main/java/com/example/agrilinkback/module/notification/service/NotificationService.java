package com.example.agrilinkback.module.notification.service;

import com.example.agrilinkback.module.notification.entity.Notification;
import com.example.agrilinkback.module.notification.mapper.NotificationMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 通知业务服务，支持广播和定向推送。
 */
@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public List<Notification> listByUser(String userName) {
        return notificationMapper.findByTargetUser(userName);
    }

    public int countUnread(String userName) {
        return notificationMapper.countUnread(userName);
    }

    public void markAsRead(Integer notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    public void markAllAsRead(String userName) {
        notificationMapper.markAllAsRead(userName);
    }

    /**
     * 发送全局广播通知（target_user = null，所有用户可见）。
     */
    public void broadcast(String title, String content, String creator) {
        notificationMapper.insert(new Notification(
                null, null, title, content, "系统通知", false, creator, LocalDateTime.now()
        ));
    }

    /**
     * 发送定向通知给指定用户。
     */
    public void notifyUser(String targetUser, String title, String content, String creator) {
        notificationMapper.insert(new Notification(
                null, targetUser, title, content, "系统通知", false, creator, LocalDateTime.now()
        ));
    }
}
