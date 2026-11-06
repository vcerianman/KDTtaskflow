package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.UserVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserVoteMapper {

    int upsert(UserVote vote);

    Optional<UserVote> findByVoterAndTarget(@Param("voterId") Long voterId,
                                            @Param("targetId") Long targetId);

    long countLikesByTargetId(@Param("targetId") Long targetId);

    long countDislikesByTargetId(@Param("targetId") Long targetId);

    long sumScoreByTargetId(@Param("targetId") Long targetId);

    int delete(@Param("voterId") Long voterId,
               @Param("targetId") Long targetId);
}
