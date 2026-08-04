package com.kdt.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kdt.taskflow.domain.Task;
import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TaskMapper {
    int insert(Task task);

    Optional<Task> findById(@Param("id") Long id);

    List<Task> search(@Param("status") TaskStatus status,
                      @Param("priority") TaskPriority priority,
                      @Param("projectId") Long projectId,
                      @Param("keyword") String keyword);

    int update(Task task);

    int deleteById(@Param("id") Long id);
}
