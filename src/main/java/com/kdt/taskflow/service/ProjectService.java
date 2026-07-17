package com.kdt.taskflow.service;

import com.kdt.taskflow.domain.ProjectStatus;
import com.kdt.taskflow.dto.ProjectRequest;
import com.kdt.taskflow.dto.ProjectResponse;

import java.util.List;

/**
 * Tầng Service: chứa business logic, điều phối Mapper, quản lý transaction.
 * <p>
 * Tách interface khỏi implementation để Controller phụ thuộc vào abstraction
 * (dễ test, dễ thay thế) — đúng tinh thần Dependency Injection.
 */
public interface ProjectService {

    ProjectResponse create(ProjectRequest request);

    ProjectResponse getById(Long id);

    List<ProjectResponse> search(ProjectStatus status, String keyword);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);
}