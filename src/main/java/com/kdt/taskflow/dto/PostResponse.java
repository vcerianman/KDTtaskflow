package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Post;

import java.time.OffsetDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String description,
        String author,
        Long reply,
        Long projectId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<PostImageResponse> images
) {
    public static PostResponse from(Post p) {
        return from(p, List.of());
    }

    public static PostResponse from(Post p, List<PostImageResponse> images) {
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getAuthor(),
                p.getReply(),
                p.getProjectId(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                images != null ? images : List.of()
        );
    }
}
