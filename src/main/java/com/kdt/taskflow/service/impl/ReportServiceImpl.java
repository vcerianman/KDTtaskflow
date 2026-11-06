package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.Report;
import com.kdt.taskflow.dto.ReportRequest;
import com.kdt.taskflow.dto.ReportResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.ReportMapper;
import com.kdt.taskflow.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    @Transactional
    public ReportResponse create(ReportRequest request, String currentUsername) {
        String effectiveAuthor = (request.author() != null && !request.author().isBlank())
                ? request.author().trim()
                : currentUsername;

        if (effectiveAuthor == null || effectiveAuthor.isBlank()) {
            effectiveAuthor = "anonymous";
        }

        Report report = request.toDomain(effectiveAuthor);
        reportMapper.insert(report);
        return getById(report.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getById(Long id) {
        Report report = reportMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find report id=" + id));
        return ReportResponse.from(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> search(String type, String author, String targetUsername, String keyword) {
        return reportMapper.search(type, author, targetUsername, keyword)
                .stream()
                .map(ReportResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = reportMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find report id=" + id);
        }
    }
}
