package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.UserReputationResponse;
import com.kdt.taskflow.dto.VoteRequest;
import com.kdt.taskflow.dto.VoteResponse;
import com.kdt.taskflow.service.UserVoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{targetUserId}")
public class UserVoteController {

    private final UserVoteService userVoteService;

    public UserVoteController(UserVoteService userVoteService) {
        this.userVoteService = userVoteService;
    }

    /**
     * POST /api/users/{targetUserId}/vote
     * Vote with a score between -100 and 100 on a user. Upserts if already voted.
     */
    @PostMapping("/vote")
    public VoteResponse vote(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal String currentUsername,
            @Valid @RequestBody VoteRequest request) {
        return userVoteService.vote(currentUsername, targetUserId, request);
    }

    /**
     * DELETE /api/users/{targetUserId}/vote
     * Remove the current user's vote on the target user.
     */
    @DeleteMapping("/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVote(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal String currentUsername) {
        userVoteService.removeVote(currentUsername, targetUserId);
    }

    /**
     * GET /api/users/{targetUserId}/reputation
     * Get reputation summary (likes, dislikes, net score, and current user's vote).
     */
    @GetMapping("/reputation")
    public UserReputationResponse getReputation(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal String currentUsername) {
        return userVoteService.getReputation(targetUserId, currentUsername);
    }
}
