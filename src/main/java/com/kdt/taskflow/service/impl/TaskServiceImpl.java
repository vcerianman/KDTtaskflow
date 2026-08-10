package com.kdt.taskflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kdt.taskflow.domain.Task;
import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;
import com.kdt.taskflow.dto.TaskRequest;
import com.kdt.taskflow.dto.TaskResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.ProjectMapper;
import com.kdt.taskflow.mapper.TaskMapper;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.TaskService;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskMapper taskMapper, ProjectMapper projectMapper, UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    private void validateTaskUsers(String assignee, String assigner) {
        if (assignee != null && !assignee.isBlank()) {
            userMapper.findByUsername(assignee)
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found: " + assignee));
        }
        if (assigner != null && !assigner.isBlank()) {
            userMapper.findByUsername(assigner)
                    .orElseThrow(() -> new ResourceNotFoundException("Assigner user not found: " + assigner));
        }
    }

    @Override
    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request) {
        Long targetProjectId = projectId != null ? projectId : request.projectId();
        if (targetProjectId != null) {
            projectMapper.findById(targetProjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find task id=" + targetProjectId));
        }

        validateTaskUsers(request.assignee(), request.assigner());

        Task task = request.toDomain(targetProjectId);
        taskMapper.insert(task);
        return getById(task.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = taskMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find task id=" + id));
        return TaskResponse.from(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> search(TaskStatus status, TaskPriority priority, Long projectId, String assignee, String assigner, String keyword) {
        return taskMapper.search(status, priority, projectId, assignee, assigner, keyword)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task existing = taskMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find task id=" + id));

        validateTaskUsers(request.assignee(), request.assigner());

        Task task = request.toDomain(existing.getProjectId());
        task.setId(id);

        int affected = taskMapper.update(task);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find task id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = taskMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find task id=" + id);
        }
    }
}
