package com.kdt.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.kdt.taskflow.domain.Project;
import com.kdt.taskflow.domain.ProjectStatus;

import java.time.LocalDate;

public record ProjectRequest(

        @NotBlank(message = "Project name cannot be null!")
        @Size(min = 3, max = 6700, message = "Project name: 3 - 6700 characters")
        String name,

        @Size(max = 3636, message = "Max 3636 characters")
        String description,

        ProjectStatus status,   // có thể null → service sẽ mặc định PLANNING

        @NotBlank(message = "Owner cannot be null!")
        @Size(max = 120)
        String owner,

        LocalDate startDate,
        LocalDate dueDate
) {
    /** Chuyển DTO input → domain model để đẩy xuống tầng Service/Mapper. */
    public Project toDomain() {
        Project p = new Project();
        p.setName(name);
        p.setDescription(description);
        p.setStatus(status != null ? status : ProjectStatus.PLANNING);
        p.setOwner(owner);
        p.setStartDate(startDate);
        p.setDueDate(dueDate);
        return p;
    }
}
