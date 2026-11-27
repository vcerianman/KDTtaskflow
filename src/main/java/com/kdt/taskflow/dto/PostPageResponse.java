package com.kdt.taskflow.dto;

import java.util.List;

public record PostPageResponse(
        List<PostResponse> items,
        Long nextCursor,
        boolean hasMore) {
}