package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.AppealRequest;
import com.kdt.taskflow.dto.AppealResponse;

import java.util.List;

public interface AppealService {

    AppealResponse create(AppealRequest request);

    AppealResponse getById(Long id);

    List<AppealResponse> search(String username, String keyword);

    void delete(Long id);
}
