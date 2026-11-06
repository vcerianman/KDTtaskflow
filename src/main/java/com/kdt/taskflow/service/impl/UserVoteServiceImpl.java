package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserVote;
import com.kdt.taskflow.dto.UserReputationResponse;
import com.kdt.taskflow.dto.VoteRequest;
import com.kdt.taskflow.dto.VoteResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.mapper.UserVoteMapper;
import com.kdt.taskflow.service.UserVoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserVoteServiceImpl implements UserVoteService {

    private final UserVoteMapper userVoteMapper;
    private final UserMapper userMapper;

    public UserVoteServiceImpl(UserVoteMapper userVoteMapper, UserMapper userMapper) {
        this.userVoteMapper = userVoteMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public VoteResponse vote(String currentUsername, Long targetUserId, VoteRequest request) {
        if (request.voteType() == null || request.voteType() < -100 || request.voteType() > 100) {
            throw new IllegalArgumentException("Vote score must be between -100 and 100");
        }

        User voter = userMapper.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found: " + currentUsername));

        User targetUser = userMapper.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found id=" + targetUserId));

        if (voter.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("You cannot vote for yourself");
        }

        UserVote userVote = new UserVote(voter.getId(), targetUser.getId(), request.voteType());
        userVoteMapper.upsert(userVote);

        UserVote saved = userVoteMapper.findByVoterAndTarget(voter.getId(), targetUser.getId())
                .orElse(userVote);

        return VoteResponse.from(saved);
    }

    @Override
    @Transactional
    public void removeVote(String currentUsername, Long targetUserId) {
        User voter = userMapper.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found: " + currentUsername));

        int affected = userVoteMapper.delete(voter.getId(), targetUserId);
        if (affected == 0) {
            throw new ResourceNotFoundException("Vote not found to remove");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserReputationResponse getReputation(Long targetUserId, String currentUsername) {
        User targetUser = userMapper.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id=" + targetUserId));

        long likes = userVoteMapper.countLikesByTargetId(targetUserId);
        long dislikes = userVoteMapper.countDislikesByTargetId(targetUserId);
        long score = userVoteMapper.sumScoreByTargetId(targetUserId);

        Integer myVote = null;
        if (currentUsername != null && !currentUsername.isBlank()) {
            User voter = userMapper.findByUsername(currentUsername).orElse(null);
            if (voter != null) {
                myVote = userVoteMapper.findByVoterAndTarget(voter.getId(), targetUserId)
                        .map(UserVote::getVoteType)
                        .orElse(null);
            }
        }

        return new UserReputationResponse(
                targetUser.getId(),
                targetUser.getUsername(),
                likes,
                dislikes,
                score,
                myVote
        );
    }
}
