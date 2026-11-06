package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.ReportRequest;
import com.kdt.taskflow.dto.ReportResponse;

import java.util.List;

public interface ReportService {

    ReportResponse create(ReportRequest request, String currentUsername);

    ReportResponse getById(Long id);

    List<ReportResponse> search(String type, String author, String targetUsername, String keyword);

    void delete(Long id);
}
