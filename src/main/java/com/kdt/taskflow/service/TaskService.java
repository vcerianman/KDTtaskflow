package com.kdt.taskflow.service;

import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;
import com.kdt.taskflow.dto.TaskRequest;
import com.kdt.taskflow.dto.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(Long projectId, TaskRequest request);

    TaskResponse getById(Long id);

    List<TaskResponse> search(TaskStatus status, TaskPriority priority, Long projectId, String assignee, String assigner, String keyword);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}
