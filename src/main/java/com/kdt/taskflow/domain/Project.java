package com.kdt.taskflow.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Domain model của "project".
 * <p>
 * Đây chỉ là một POJO thuần — KHÔNG có annotation ORM nào cả. MyBatis đọc từng
 * dòng trong bảng {@code project} rồi map cột sang các thuộc tính ở đây
 * (xem {@code resultMap} trong ProjectMapper.xml).
 */
public class Project {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private String owner;
    private LocalDate startDate;
    private LocalDate dueDate;
    private OffsetDateTime createdAt;   // do DB tự set (DEFAULT now())
    private OffsetDateTime updatedAt;   // cập nhật bằng now() trong câu UPDATE

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}