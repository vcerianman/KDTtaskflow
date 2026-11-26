package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Post;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequest(
        @Size(max = 255, message = "Title max 255 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @Size(max = 120, message = "Author max 120 characters")
        String author,

        Long reply,

        Long projectId,

        @Valid
        @Size(max = 4, message = "Maximum 4 images allowed per post")
        List<PostImageRequest> images
) {
    public Post toDomain(String defaultAuthor) {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setAuthor(author != null && !author.isBlank() ? author : defaultAuthor);
        post.setReply(reply);
        post.setProjectId(projectId);
        return post;
    }
}
