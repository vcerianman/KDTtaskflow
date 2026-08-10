package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Task;
import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String assignee,
        String assigner,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    /** Chuyển domain model → DTO output. */
    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getProjectId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getAssignee(),
                t.getAssigner(),
                t.getDueDate(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
