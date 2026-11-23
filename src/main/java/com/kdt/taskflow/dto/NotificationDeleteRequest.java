package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record NotificationDeleteRequest(
        @JsonAlias({"notificationIds", "ids", "notification_ids"})
        List<Long> notificationIds,

        @JsonAlias({"userId", "recipientId", "user_id", "recipient_id"})
        Long userId
) {
}

