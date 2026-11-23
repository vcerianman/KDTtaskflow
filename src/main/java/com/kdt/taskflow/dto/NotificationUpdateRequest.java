package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Supports "half requests" — any null field will be ignored and left unchanged in database.
 * Supports updating multiple notification IDs and/or by recipient userId.
 */
public record NotificationUpdateRequest(
        @JsonAlias({"notificationIds", "ids", "notification_ids"})
        List<Long> notificationIds,

        @JsonAlias({"userId", "recipientId", "user_id", "recipient_id"})
        Long userId,

        @JsonProperty("is_read")
        @JsonAlias({"isRead", "read", "is_read"})
        Boolean isRead,

        @JsonProperty("is_seen")
        @JsonAlias({"isSeen", "seen", "is_seen"})
        Boolean isSeen,

        String message,

        String link
) {
}

