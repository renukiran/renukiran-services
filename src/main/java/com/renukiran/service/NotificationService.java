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

    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setIsRead(true);
        return NotificationResponse.from(notificationRepository.save(notification));
    }
}
