package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        UserRole role,
        UserStatus status,
        boolean deleted,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFullName(),
                u.getRole(),
                u.getStatus(),
                u.isDeleted(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
