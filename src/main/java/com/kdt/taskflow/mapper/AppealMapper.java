package com.kdt.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kdt.taskflow.domain.Appeal;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AppealMapper {
    int insert(Appeal appeal);

    Optional<Appeal> findById(@Param("id") Long id);

    List<Appeal> search(@Param("username") String username,
                        @Param("keyword") String keyword);

    int deleteById(@Param("id") Long id);
}
