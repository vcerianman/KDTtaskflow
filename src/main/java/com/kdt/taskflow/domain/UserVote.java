package com.kdt.taskflow.domain;

import java.time.OffsetDateTime;

public class UserVote {
    private Long voterId;
    private Long targetId;
    private Integer voteType; // vote score between -100 and 100
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserVote() {}

    public UserVote(Long voterId, Long targetId, Integer voteType) {
        this.voterId = voterId;
        this.targetId = targetId;
        this.voteType = voteType;
    }

    public Long getVoterId() { return voterId; }
    public void setVoterId(Long voterId) { this.voterId = voterId; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getVoteType() { return voteType; }
    public void setVoteType(Integer voteType) { this.voteType = voteType; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
