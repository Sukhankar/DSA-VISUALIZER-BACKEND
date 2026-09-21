package com.codeloom.dsa.notification.dto;

import com.codeloom.dsa.notification.entity.Notification;
import com.codeloom.dsa.notification.entity.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        OffsetDateTime createdAt
) {
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
