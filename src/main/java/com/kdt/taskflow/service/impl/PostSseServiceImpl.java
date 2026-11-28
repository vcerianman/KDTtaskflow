package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.dto.PostEvent;
import com.kdt.taskflow.dto.PostResponse;
import com.kdt.taskflow.service.PostSseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PostSseServiceImpl implements PostSseService {

    private static final Logger log = LoggerFactory.getLogger(PostSseServiceImpl.class);

    // Default timeout set to 30 minutes
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;
    // Client reconnect interval hint: 3 seconds
    private static final long RECONNECT_TIME = 3000L;

    private record SseSubscriber(
            String id,
            SseEmitter emitter,
            Long projectId,
            String username) {
    }

    private final Map<String, SseSubscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long projectId, String username) {
        String subscriberId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        SseSubscriber subscriber = new SseSubscriber(subscriberId, emitter, projectId, username);

        subscribers.put(subscriberId, subscriber);
        log.info("SSE client connected: id={}, user={}, projectId={}, activeSubscribers={}",
                subscriberId, username, projectId, subscribers.size());

        emitter.onCompletion(() -> {
            log.info("SSE client completed: id={}", subscriberId);
            subscribers.remove(subscriberId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE client timed out: id={}", subscriberId);
            subscribers.remove(subscriberId);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });

        emitter.onError(throwable -> {
            log.warn("SSE client error: id={}, error={}", subscriberId, throwable.getMessage());
            subscribers.remove(subscriberId);
        });

        // Send initial handshake event to confirm the stream is active
        try {
            emitter.send(SseEmitter.event()
                    .id(subscriberId)
                    .name("CONNECTED")
                    .reconnectTime(RECONNECT_TIME)
                    .data(PostEvent.connected(username, projectId), MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.warn("Failed to send initial handshake to SSE client id={}: {}", subscriberId, e.getMessage());
            subscribers.remove(subscriberId);
        }

        return emitter;
    }

    @Override
    public void broadcast(PostEvent event) {
        if (subscribers.isEmpty()) {
            return;
        }

        log.debug("Broadcasting post event: type={}, postId={}, projectId={}",
                event.eventType(), event.postId(), event.projectId());

        subscribers.forEach((id, sub) -> {
            boolean shouldReceive = sub.projectId() == null
                    || event.projectId() == null
                    || sub.projectId().equals(event.projectId());

            if (shouldReceive) {
                try {
                    sub.emitter().send(SseEmitter.event()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name(event.eventType())
                            .data(event, MediaType.APPLICATION_JSON));
                } catch (Exception ex) {
                    log.debug("Error delivering SSE event to subscriber id={}, removing: {}", id, ex.getMessage());
                    subscribers.remove(id);
                }
            }
        });
    }

    @Override
    public void broadcastCreated(PostResponse post) {
        broadcast(PostEvent.created(post));
    }

    @Override
    public void broadcastUpdated(PostResponse post) {
        broadcast(PostEvent.updated(post));
    }

    @Override
    public void broadcastDeleted(Long postId, Long projectId) {
        broadcast(PostEvent.deleted(postId, projectId));
    }

    @Override
    public int getActiveSubscriberCount() {
        return subscribers.size();
    }

    @Scheduled(fixedDelay = 25000)
    public void sendHeartbeat() {
        if (subscribers.isEmpty()) {
            return;
        }

        subscribers.forEach((id, sub) -> {
            try {
                sub.emitter().send(SseEmitter.event()
                        .comment("keepalive")
                        .name("HEARTBEAT")
                        .data(PostEvent.heartbeat(), MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                log.debug("Heartbeat failed for subscriber id={}, removing: {}", id, ex.getMessage());
                subscribers.remove(id);
            }
        });
    }
}