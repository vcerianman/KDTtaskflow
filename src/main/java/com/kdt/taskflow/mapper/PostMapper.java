package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    int insert(Post post);

    Optional<Post> findById(@Param("id") Long id);

    List<Post> search(@Param("projectId") Long projectId,
                      @Param("author") String author,
                      @Param("reply") Long reply,
                      @Param("keyword") String keyword,
                      @Param("topLevelOnly") Boolean topLevelOnly);

    int update(Post post);

    int deleteById(@Param("id") Long id);
}
