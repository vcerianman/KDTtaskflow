package com.kdt.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "New password cannot be blank!")
        @Size(min = 6, max = 100, message = "New password must be at least 6 characters")
        String newPassword
) {}
