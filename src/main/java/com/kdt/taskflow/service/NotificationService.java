package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.NotificationCreateRequest;
import com.kdt.taskflow.dto.NotificationDeleteRequest;
import com.kdt.taskflow.dto.NotificationResponse;
import com.kdt.taskflow.dto.NotificationUpdateRequest;

import java.util.List;

public interface NotificationService {

    /**
     * GET /api/notifications/{userId}
     * Fetch user notifications (newest first). Supports optional limit and unreadOnly filters.
     */
    List<NotificationResponse> getByUserId(Long userId, Integer limit, Boolean unreadOnly);

    /**
     * GET /api/notifications/n/{userId}
     * Count unread (is_read = false) notifications for badge.
     */
    long countUnread(Long userId);

    /**
     * POST /api/notifications
     * Create notifications for one or multiple recipients, and push via SSE.
     */
    List<NotificationResponse> createNotifications(NotificationCreateRequest request);

    /**
     * PATCH/PUT /api/notifications or /api/notifications/{id}
     * Update notifications with "half request" support (ignoring null fields).
     * If is_read becomes true, automatically records seen_at = NOW().
     */
    int updateNotifications(Long singleId, NotificationUpdateRequest request);

    /**
     * DELETE /api/notifications or /api/notifications/{id}
     * Delete notifications by single ID, multiple IDs, or all for a user.
     */
    int deleteNotifications(Long singleId, NotificationDeleteRequest request, Long queryUserId);

    /**
     * Background scheduled job that periodically deletes rows whose seen_at timestamp has passed 120 hours.
     */
    int cleanupExpiredSeenNotifications();
}

