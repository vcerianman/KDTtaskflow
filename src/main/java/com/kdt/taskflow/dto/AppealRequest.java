package com.kdt.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.kdt.taskflow.domain.Appeal;

public record AppealRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(max = 80, message = "Username must be under 80 characters")
        String username,

        @NotBlank(message = "Reason cannot be blank")
        String reason
) {
    public Appeal toDomain() {
        Appeal appeal = new Appeal();
        appeal.setUsername(username != null ? username.trim() : null);
        appeal.setReason(reason != null ? reason.trim() : null);
        return appeal;
    }
}
