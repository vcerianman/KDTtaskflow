package com.kdt.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NotificationCreateRequest(
        @NotEmpty(message = "recipientIds cannot be empty")
        List<Long> recipientIds,

        Long actorId,

        @NotBlank(message = "type cannot be blank")
        String type,

        @NotBlank(message = "message cannot be blank")
        String message,

        String link
) {
}

