package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long userId,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUser() != null ? n.getUser().getId() : null,
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}