package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostImageRequest(
        @NotBlank(message = "Image url cannot be blank")
        @JsonAlias({"url", "imageUrl", "image_url"})
        String imageUrl,

        @Size(max = 255, message = "Public ID max 255 characters")
        @JsonAlias({"publicId", "public_id"})
        String publicId,

        @Min(value = 0, message = "displayOrder must be between 0 and 3")
        @Max(value = 3, message = "displayOrder must be between 0 and 3")
        @JsonAlias({"displayOrder", "display_order"})
        Integer displayOrder
) {
}

