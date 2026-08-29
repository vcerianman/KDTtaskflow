package com.kdt.taskflow.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckPasswordRequest(
        @NotBlank(message = "Password cannot be blank")
        String password
) {}
