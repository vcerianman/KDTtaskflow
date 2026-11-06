package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Post;

import java.time.OffsetDateTime;

public record PostResponse(
        Long id,
        String title,
        String description,
        String author,
        Long reply,
        Long projectId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static PostResponse from(Post p) {
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getAuthor(),
                p.getReply(),
                p.getProjectId(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
