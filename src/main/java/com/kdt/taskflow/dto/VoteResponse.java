package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.UserVote;
import java.time.OffsetDateTime;

public record VoteResponse(
        Long voterId,
        Long targetId,
        Integer voteType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static VoteResponse from(UserVote vote) {
        return new VoteResponse(
                vote.getVoterId(),
                vote.getTargetId(),
                vote.getVoteType(),
                vote.getCreatedAt(),
                vote.getUpdatedAt()
        );
    }
}
