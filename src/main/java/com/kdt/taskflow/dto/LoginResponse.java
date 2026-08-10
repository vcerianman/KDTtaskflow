package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.UserRole;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String username,
        String email,
        UserRole role
) {
    public static LoginResponse of(String token, String username, String email, UserRole role) {
        return new LoginResponse(token, "Bearer", username, email, role);
    }
}
