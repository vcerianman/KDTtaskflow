package com.kdt.taskflow.dto;

import java.util.List;

public record TaskPageResponse(
        List<TaskResponse> items,
        Long nextCursor,
        boolean hasMore) {
}