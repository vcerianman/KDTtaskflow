package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Project;
import com.kdt.taskflow.domain.ProjectStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Dữ liệu TRẢ VỀ cho client (output DTO).
 * <p>
 * Tách khỏi domain model để có toàn quyền kiểm soát cái gì lộ ra API — đây là
 * lý do mình không trả thẳng entity ra ngoài.
 */
public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        String owner,
        LocalDate startDate,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    /** Chuyển domain model → DTO output. */
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                p.getOwner(),
                p.getStartDate(),
                p.getDueDate(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}