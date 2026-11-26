package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostImageMapper {

    int insertBatch(@Param("images") List<PostImage> images);

    List<PostImage> findByPostId(@Param("postId") Long postId);

    List<PostImage> findByPostIds(@Param("postIds") List<Long> postIds);

    int deleteByPostId(@Param("postId") Long postId);
}

