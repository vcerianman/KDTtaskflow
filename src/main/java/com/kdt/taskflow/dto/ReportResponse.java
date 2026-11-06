package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.Report;

import java.time.OffsetDateTime;

public record ReportResponse(
        Long id,
        String type,
        String author,
        String targetUsername,
        String reason,
        String explanation,
        OffsetDateTime createdAt
) {
    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getType(),
                report.getAuthor(),
                report.getTargetUsername(),
                report.getReason(),
                report.getExplanation(),
                report.getCreatedAt()
        );
    }
}
