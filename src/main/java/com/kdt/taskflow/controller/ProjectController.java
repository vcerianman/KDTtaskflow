package com.kdt.taskflow.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.kdt.taskflow.domain.ProjectStatus;
import com.kdt.taskflow.dto.ProjectRequest;
import com.kdt.taskflow.dto.ProjectResponse;
import com.kdt.taskflow.service.ProjectService;

import java.net.URI;
import java.util.List;

/**
 * Tầng Controller: điểm vào của mọi HTTP request cho tài nguyên "project".
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** POST /api/projects — tạo mới. Trả 201 Created + header Location. */
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        ProjectResponse created = projectService.create(request);
        URI location = uriBuilder.path("/api/projects/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/projects — danh sách, có thể lọc theo status và keyword.
     * Ví dụ: /api/projects?status=ACTIVE&keyword=portal
     */
    @GetMapping
    public List<ProjectResponse> search(@RequestParam(required = false) ProjectStatus status,
                                        @RequestParam(required = false) String keyword) {
        return projectService.search(status, keyword);
    }

    /** GET /api/projects/{id} — lấy 1 project. Trả 404 nếu không có. */
    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    /** PUT /api/projects/{id} or PATCH /api/projects/{id} — cập nhật. Trả 404 nếu id không tồn tại. */
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ProjectResponse update(@PathVariable Long id,
                                  @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    /** DELETE /api/projects/{id} — xóa. Trả 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}