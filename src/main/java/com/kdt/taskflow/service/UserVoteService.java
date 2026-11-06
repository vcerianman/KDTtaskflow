package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.UserReputationResponse;
import com.kdt.taskflow.dto.VoteRequest;
import com.kdt.taskflow.dto.VoteResponse;

public interface UserVoteService {

    VoteResponse vote(String currentUsername, Long targetUserId, VoteRequest request);

    void removeVote(String currentUsername, Long targetUserId);

    UserReputationResponse getReputation(Long targetUserId, String currentUsername);
}
