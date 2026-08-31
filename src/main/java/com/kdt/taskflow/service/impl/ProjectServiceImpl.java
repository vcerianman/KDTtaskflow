package com.kdt.taskflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kdt.taskflow.domain.Project;
import com.kdt.taskflow.domain.ProjectStatus;
import com.kdt.taskflow.dto.ProjectRequest;
import com.kdt.taskflow.dto.ProjectResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.ProjectMapper;
import com.kdt.taskflow.service.ProjectService;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    // Constructor injection: Spring tự tiêm ProjectMapper vào đây.
    // Không cần @Autowired vì class chỉ có 1 constructor.
    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = request.toDomain();
        projectMapper.insert(project);              // sau khi insert, project.id được gán
        // Đọc lại để lấy created_at/updated_at do DB set → trả về bản ghi đầy đủ
        return getById(project.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find project id=" + id));
        return ProjectResponse.from(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> search(ProjectStatus status, String keyword) {
        return projectMapper.search(status, keyword)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project existing = projectMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find project id=" + id));

        if (request.name() != null && !request.name().isBlank()) {
            existing.setName(request.name().trim());
        }

        if (request.description() != null) {
            existing.setDescription(request.description().trim());
        }

        if (request.status() != null) {
            existing.setStatus(request.status());
        }

        if (request.owner() != null && !request.owner().isBlank()) {
            existing.setOwner(request.owner().trim());
        }

        if (request.startDate() != null) {
            existing.setStartDate(request.startDate());
        }

        if (request.dueDate() != null) {
            existing.setDueDate(request.dueDate());
        }

        int affected = projectMapper.update(existing);
        if (affected == 0) {
            // update trả 0 dòng nghĩa là id không tồn tại
            throw new ResourceNotFoundException("Cannot find project id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = projectMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find project id=" + id);
        }
    }
}