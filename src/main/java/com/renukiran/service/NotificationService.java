package com.renukiran.service;

import com.renukiran.dto.NotificationResponse;
import com.renukiran.entity.Notification;
import com.renukiran.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public long getUnreadCount() {
        return notificationRepository.countByIsRead(false);
    }

    public NotificationResponse createNotification(String message) {
        Notification n = Notification.builder()
                .message(message)
                .isRead(false)
                .build();
        return NotificationResponse.from(notificationRepository.save(n));
    }

    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setIsRead(true);
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).toList();
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}
