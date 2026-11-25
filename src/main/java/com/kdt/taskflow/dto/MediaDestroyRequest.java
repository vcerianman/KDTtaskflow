package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record MediaDestroyRequest(
        @NotBlank(message = "publicId cannot be blank")
        @JsonAlias({"publicId", "public_id"})
        String publicId,

        @JsonAlias({"resourceType", "resource_type"})
        String resourceType
) {
}

