package com.kdt.taskflow.controller;

import com.kdt.taskflow.domain.TaskPriority;
import com.kdt.taskflow.domain.TaskStatus;
import com.kdt.taskflow.dto.TaskRequest;
import com.kdt.taskflow.dto.TaskResponse;
import com.kdt.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /api/projects/{projectId}/tasks — Tcreate 1 task for project.
     */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            UriComponentsBuilder uriBuilder) {

        TaskResponse created = taskService.create(projectId, request);

        URI location = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/projects/{projectId}/tasks and /api/project/{projectId}/tasks
     * Returns all tasks associated with the specified project ID.
     */
    @GetMapping({ "/projects/{projectId}/tasks", "/project/{projectId}/tasks" })
    public List<TaskResponse> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getByProjectId(projectId);
    }

    /**
     * GET /api/tasks - Get list of tasks - and sortable
     * EX:
     * /api/tasks?status=IN_PROGRESS&priority=HIGH&projectId=1&assignee=john_doe&keyword=feature
     */
    @GetMapping("/tasks")
    public List<TaskResponse> search(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String assigner,
            @RequestParam(required = false) String keyword) {

        return taskService.search(status, priority, projectId, assignee, assigner, keyword);
    }

    /** GET /api/tasks/{id} — get a specific task - ret 404 if not found. */
    @GetMapping("/tasks/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    /** PUT /api/tasks/{id} or PATCH /api/tasks/{id} — update task by id (partial update supported) - return 404 if not found. */
    @RequestMapping(value = "/tasks/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public TaskResponse update(@PathVariable Long id,
            @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    /** DELETE /api/tasks/{id} — delete task - return 204 No Content. */
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
