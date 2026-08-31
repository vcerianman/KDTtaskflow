package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Appeal;

import java.time.OffsetDateTime;

public record AppealResponse(
        Long id,
        String username,
        String reason,
        OffsetDateTime createdAt
) {
    public static AppealResponse from(Appeal appeal) {
        return new AppealResponse(
                appeal.getId(),
                appeal.getUsername(),
                appeal.getReason(),
                appeal.getCreatedAt()
        );
    }
}
