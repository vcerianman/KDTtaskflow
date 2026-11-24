package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.Notification;
import com.kdt.taskflow.dto.NotificationCreateRequest;
import com.kdt.taskflow.dto.NotificationDeleteRequest;
import com.kdt.taskflow.dto.NotificationResponse;
import com.kdt.taskflow.dto.NotificationUpdateRequest;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.NotificationMapper;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.NotificationService;
import com.kdt.taskflow.service.NotificationSseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final NotificationSseService notificationSseService;

    public NotificationServiceImpl(NotificationMapper notificationMapper,
                                   UserMapper userMapper,
                                   NotificationSseService notificationSseService) {
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.notificationSseService = notificationSseService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUserId(Long userId, Integer limit, Boolean unreadOnly) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));

        int effectiveLimit = (limit != null && limit > 0) ? limit : 20;

        return notificationMapper.findByRecipientId(userId, unreadOnly, effectiveLimit)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));

        return notificationMapper.countUnreadByRecipientId(userId);
    }

    @Override
    @Transactional
    public List<NotificationResponse> createNotifications(NotificationCreateRequest request) {
        if (request.recipientIds() == null || request.recipientIds().isEmpty()) {
            throw new IllegalArgumentException("recipientIds cannot be empty");
        }

        List<NotificationResponse> results = new ArrayList<>();

        // De-duplicate recipient IDs while preserving order
        List<Long> distinctRecipients = request.recipientIds().stream().distinct().toList();

        for (Long recipientId : distinctRecipients) {
            // Verify recipient exists
            if (userMapper.findById(recipientId).isEmpty()) {
                log.warn("Skipping notification creation: recipient id={} does not exist", recipientId);
                continue;
            }

            // Check if duplicate notification already exists with (recipient_id, type, message, actor_id)
            Optional<Notification> existingOpt = notificationMapper.findDuplicate(
                    recipientId,
                    request.type(),
                    request.message(),
                    request.actorId()
            );

            Notification notification;
            if (existingOpt.isPresent()) {
                Notification existing = existingOpt.get();
                log.info("Duplicate notification found (id={}) for recipient {}. Resetting is_read=false, is_seen=false, seen_at=null, and created_at=now()",
                        existing.getId(), recipientId);

                notificationMapper.refreshDuplicate(existing.getId(), request.link());

                // Refetch refreshed row with updated created_at and reset flags
                notification = notificationMapper.findById(existing.getId()).orElse(existing);
            } else {
                notification = new Notification();
                notification.setRecipientId(recipientId);
                notification.setActorId(request.actorId());
                notification.setType(request.type());
                notification.setMessage(request.message());
                notification.setLink(request.link());
                notification.setRead(false);
                notification.setSeen(false);
                notification.setSeenAt(null);

                notificationMapper.insert(notification);
            }

            NotificationResponse response = NotificationResponse.from(notification);
            results.add(response);

            // Push notification in real-time over SSE to connected client
            notificationSseService.sendNotification(recipientId, response);
        }

        return results;
    }

    @Override
    @Transactional
    public int updateNotifications(Long singleId, NotificationUpdateRequest request) {
        List<Long> targetIds = null;
        Long targetUserId = null;

        if (singleId != null) {
            targetIds = List.of(singleId);
        } else if (request != null) {
            if (request.notificationIds() != null && !request.notificationIds().isEmpty()) {
                targetIds = request.notificationIds();
            }
            targetUserId = request.userId();
        }

        if ((targetIds == null || targetIds.isEmpty()) && targetUserId == null) {
            throw new IllegalArgumentException("Must specify notificationIds or userId to update notifications");
        }

        Boolean isRead = request != null ? request.isRead() : null;
        Boolean isSeen = request != null ? request.isSeen() : null;
        String message = request != null ? request.message() : null;
        String link = request != null ? request.link() : null;

        OffsetDateTime seenAt = null;
        // Requirement: "If isread is true, the backend records seen_at = NOW()"
        if (Boolean.TRUE.equals(isRead)) {
            seenAt = OffsetDateTime.now();
            if (isSeen == null) {
                isSeen = true;
            }
        } else if (Boolean.TRUE.equals(isSeen)) {
            seenAt = OffsetDateTime.now();
        }

        return notificationMapper.updateDynamic(
                targetIds,
                targetUserId,
                isRead,
                isSeen,
                seenAt,
                message,
                link
        );
    }

    @Override
    @Transactional
    public int deleteNotifications(Long singleId, NotificationDeleteRequest request, Long queryUserId) {
        List<Long> targetIds = null;
        Long targetUserId = queryUserId;

        if (singleId != null) {
            targetIds = List.of(singleId);
        } else if (request != null) {
            if (request.notificationIds() != null && !request.notificationIds().isEmpty()) {
                targetIds = request.notificationIds();
            }
            if (request.userId() != null) {
                targetUserId = request.userId();
            }
        }

        if ((targetIds == null || targetIds.isEmpty()) && targetUserId == null) {
            throw new IllegalArgumentException("Must specify notificationIds or userId to delete notifications");
        }

        return notificationMapper.deleteDynamic(targetIds, targetUserId);
    }

    /**
     * Requirement: "a single background @Scheduled job periodically deletes rows
     * whose seen_at timestamp has passed about 5 days. (120 hours)"
     * Runs every hour.
     */
    @Scheduled(fixedDelay = 3600000)
    @Transactional
    @Override
    public int cleanupExpiredSeenNotifications() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusHours(120);
        int deleted = notificationMapper.deleteExpiredSeenNotifications(cutoff);
        if (deleted > 0) {
            log.info("Cleanup job removed {} notifications seen older than 120 hours (cutoff: {})", deleted, cutoff);
        }
        return deleted;
    }
}
