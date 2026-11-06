package com.kdt.taskflow.dto;

public record UserReputationResponse(
        Long targetId,
        String targetUsername,
        long likes,
        long dislikes,
        long reputationScore,
        Integer myVote // between -100 and 100, or null if current user hasn't voted
) {
}
