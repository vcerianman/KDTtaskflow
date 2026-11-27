package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.Task;
import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TaskMapper {

    int insert(Task task);

    Optional<Task> findById(@Param("id") Long id);

    List<Task> search(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("projectId") Long projectId,
            @Param("assignee") String assignee,
            @Param("assigner") String assigner,
            @Param("keyword") String keyword);

    List<Task> searchPaged(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("projectId") Long projectId,
            @Param("assignee") String assignee,
            @Param("assigner") String assigner,
            @Param("keyword") String keyword,
            @Param("cursor") Long cursor,
            @Param("limitPlusOne") int limitPlusOne);

    int update(Task task);

    int deleteById(@Param("id") Long id);
}