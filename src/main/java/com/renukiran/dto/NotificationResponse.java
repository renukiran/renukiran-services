package com.renukiran.dto;

import com.renukiran.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private String time;        // relative time string: "2 hrs ago"
    private Boolean isRead;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .time(relativeTime(n.getCreatedAt()))
                .isRead(n.getIsRead())
                .build();
    }

    private static String relativeTime(LocalDateTime createdAt) {
        if (createdAt == null) return "just now";
        Duration diff = Duration.between(createdAt, LocalDateTime.now());
        long mins = diff.toMinutes();
        if (mins < 1) return "just now";
        if (mins < 60) return mins + " min" + (mins == 1 ? "" : "s") + " ago";
        long hrs = diff.toHours();
        if (hrs < 24) return hrs + " hr" + (hrs == 1 ? "" : "s") + " ago";
        long days = diff.toDays();
        return days + " day" + (days == 1 ? "" : "s") + " ago";
    }
}
