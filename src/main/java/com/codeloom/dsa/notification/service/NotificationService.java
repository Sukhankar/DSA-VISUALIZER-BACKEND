package com.codeloom.dsa.notification.service;

import com.codeloom.dsa.notification.dto.NotificationDto;
import com.codeloom.dsa.notification.entity.Notification;
import com.codeloom.dsa.notification.entity.NotificationType;
import com.codeloom.dsa.notification.repository.NotificationRepository;
import com.codeloom.dsa.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    // Concurrent mapping of User ID to active SSE emitters
    private final Map<UUID, List<SseEmitter>> userEmittersMap = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(User user, int limit) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, Math.min(limit, 50)))
                .stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(UUID notificationId, User user) {
        notificationRepository.findByIdAndUser(notificationId, user).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    @Transactional
    public NotificationDto sendNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification(user, title, message, type);
        notification = notificationRepository.save(notification);
        NotificationDto dto = NotificationDto.fromEntity(notification);

        // Push real-time event to SSE listeners if user is connected
        pushToUser(user.getId(), dto);

        return dto;
    }

    public SseEmitter subscribe(User user) {
        UUID userId = user.getId();
        SseEmitter emitter = new SseEmitter(180_000L); // 3 minutes timeout

        userEmittersMap.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        // Send initial connection ACK
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data("Connected to CodeLoom Notification Stream"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        return emitter;
    }

    private void pushToUser(UUID userId, NotificationDto dto) {
        List<SseEmitter> emitters = userEmittersMap.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(dto));
            } catch (Exception e) {
                log.debug("Failed to deliver SSE notification to user {}, removing emitter", userId);
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmittersMap.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmittersMap.remove(userId);
            }
        }
    }
}
