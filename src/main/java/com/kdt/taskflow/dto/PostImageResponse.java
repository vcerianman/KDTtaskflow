package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdt.taskflow.domain.PostImage;

public record PostImageResponse(
        @JsonProperty("post_id")      Long postId,
        @JsonProperty("image_url")    String imageUrl,
        @JsonProperty("public_id")    String publicId,
        @JsonProperty("display_order") Integer displayOrder
) {
    public static PostImageResponse from(PostImage image) {
        return new PostImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getPublicId(),
                image.getDisplayOrder()
        );
    }
}

