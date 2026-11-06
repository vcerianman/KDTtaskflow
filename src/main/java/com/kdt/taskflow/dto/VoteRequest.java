package com.kdt.taskflow.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull(message = "Vote score is required")
        @Min(value = -100, message = "Vote score must be at least -100")
        @Max(value = 100, message = "Vote score must be at most 100")
        Integer voteType
) {
}
