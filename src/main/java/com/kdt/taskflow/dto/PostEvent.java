package com.kdt.taskflow.dto;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing an event sent over the Server-Sent Events (SSE) stream.
 */
public record PostEvent(
        String eventType,       // "CONNECTED", "POST_CREATED", "POST_UPDATED", "POST_DELETED", "HEARTBEAT"
        Long projectId,         // Associated project id (null for global posts or heartbeat)
        Long postId,            // Post id affected
        PostResponse post,      // Full post payload (for created or updated events)
        OffsetDateTime timestamp
) {

    public static PostEvent connected(String username, Long projectId) {
        return new PostEvent(
                "CONNECTED",
                projectId,
                null,
                null,
                OffsetDateTime.now()
        );
    }

    public static PostEvent created(PostResponse post) {
        return new PostEvent(
                "POST_CREATED",
                post.projectId(),
                post.id(),
                post,
                OffsetDateTime.now()
        );
    }

    public static PostEvent updated(PostResponse post) {
        return new PostEvent(
                "POST_UPDATED",
                post.projectId(),
                post.id(),
                post,
                OffsetDateTime.now()
        );
    }

    public static PostEvent deleted(Long postId, Long projectId) {
        return new PostEvent(
                "POST_DELETED",
                projectId,
                postId,
                null,
                OffsetDateTime.now()
        );
    }

    public static PostEvent heartbeat() {
        return new PostEvent(
                "HEARTBEAT",
                null,
                null,
                null,
                OffsetDateTime.now()
        );
    }
}

