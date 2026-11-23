package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.dto.NotificationResponse;
import com.kdt.taskflow.service.NotificationSseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSseServiceImpl implements NotificationSseService {

    private static final Logger log = LoggerFactory.getLogger(NotificationSseServiceImpl.class);

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes
    private static final long RECONNECT_TIME = 3000L;       // 3 seconds

    private record SseSubscriber(
            String id,
            SseEmitter emitter,
            Long userId,
            String username
    ) {}

    private final Map<String, SseSubscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long userId, String username) {
        String subscriberId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        SseSubscriber subscriber = new SseSubscriber(subscriberId, emitter, userId, username);

        subscribers.put(subscriberId, subscriber);
        log.info("Notification SSE client connected: subscriberId={}, userId={}, username={}, activeCount={}",
                subscriberId, userId, username, subscribers.size());

        emitter.onCompletion(() -> {
            log.info("Notification SSE client completed: subscriberId={}", subscriberId);
            subscribers.remove(subscriberId);
        });

        emitter.onTimeout(() -> {
            log.warn("Notification SSE client timed out: subscriberId={}", subscriberId);
            emitter.complete();
            subscribers.remove(subscriberId);
        });

        emitter.onError(throwable -> {
            log.warn("Notification SSE client error: subscriberId={}, error={}", subscriberId, throwable.getMessage());
            emitter.complete();
            subscribers.remove(subscriberId);
        });

        // Send initial handshake event
        try {
            Map<String, Object> handshake = Map.of(
                    "eventType", "CONNECTED",
                    "userId", userId != null ? userId : -1L,
                    "username", username != null ? username : "",
                    "timestamp", OffsetDateTime.now().toString()
            );
            emitter.send(SseEmitter.event()
                    .id(subscriberId)
                    .name("CONNECTED")
                    .reconnectTime(RECONNECT_TIME)
                    .data(handshake, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.warn("Failed to send initial handshake to notification SSE client {}: {}", subscriberId, e.getMessage());
            subscribers.remove(subscriberId);
        }

        return emitter;
    }

    @Override
    public void sendNotification(Long recipientId, NotificationResponse notification) {
        if (subscribers.isEmpty() || recipientId == null) {
            return;
        }

        subscribers.forEach((id, sub) -> {
            if (recipientId.equals(sub.userId())) {
                try {
                    sub.emitter().send(SseEmitter.event()
                            .id(String.valueOf(notification.id()))
                            .name("NOTIFICATION")
                            .data(notification, MediaType.APPLICATION_JSON));
                    log.debug("Delivered notification {} to SSE subscriber {} (user {})",
                            notification.id(), id, recipientId);
                } catch (Exception ex) {
                    log.debug("Error delivering notification to subscriber {}, removing: {}", id, ex.getMessage());
                    sub.emitter().complete();
                    subscribers.remove(id);
                }
            }
        });
    }

    @Override
    public void sendNotifications(NotificationResponse notification) {
        if (notification != null && notification.recipientId() != null) {
            sendNotification(notification.recipientId(), notification);
        }
    }

    @Override
    public int getActiveSubscriberCount() {
        return subscribers.size();
    }

    /**
     * Send heartbeat every 25 seconds to keep proxies (Nginx, ALB, Cloudflare, Render) alive.
     */
    @Scheduled(fixedDelay = 25000)
    public void sendHeartbeat() {
        if (subscribers.isEmpty()) {
            return;
        }

        Map<String, Object> ping = Map.of(
                "eventType", "HEARTBEAT",
                "timestamp", OffsetDateTime.now().toString()
        );

        subscribers.forEach((id, sub) -> {
            try {
                sub.emitter().send(SseEmitter.event()
                        .comment("keepalive")
                        .name("HEARTBEAT")
                        .data(ping, MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                log.debug("Notification heartbeat failed for subscriber {}, removing: {}", id, ex.getMessage());
                sub.emitter().complete();
                subscribers.remove(id);
            }
        });
    }
}

