package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdt.taskflow.domain.Notification;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        Long recipientId,
        Long actorId,
        String type,
        String message,
        String link,
        @JsonProperty("is_read")
        Boolean isRead,
        @JsonProperty("is_seen")
        Boolean isSeen,
        OffsetDateTime seenAt,
        OffsetDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getRecipientId(),
                n.getActorId(),
                n.getType(),
                n.getMessage(),
                n.getLink(),
                n.isRead(),
                n.isSeen(),
                n.getSeenAt(),
                n.getCreatedAt()
        );
    }
}

