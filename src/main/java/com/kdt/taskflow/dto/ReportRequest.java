package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @Size(max = 50, message = "Type max 50 characters")
        String type,

        @Size(max = 120, message = "Author max 120 characters")
        String author,

        @NotBlank(message = "Target username cannot be blank")
        @Size(max = 120, message = "Target username max 120 characters")
        String targetUsername,

        @NotBlank(message = "Reason cannot be blank")
        @Size(max = 255, message = "Reason max 255 characters")
        String reason,

        String explanation
) {
    public Report toDomain(String defaultAuthor) {
        Report report = new Report();
        report.setType(type != null && !type.isBlank() ? type.trim() : "GENERAL");
        report.setAuthor(author != null && !author.isBlank() ? author.trim() : defaultAuthor);
        report.setTargetUsername(targetUsername != null ? targetUsername.trim() : null);
        report.setReason(reason != null ? reason.trim() : null);
        report.setExplanation(explanation != null ? explanation.trim() : null);
        return report;
    }
}
