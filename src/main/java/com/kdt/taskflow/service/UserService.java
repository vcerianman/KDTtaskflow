package com.kdt.taskflow.service;

import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> search(UserRole role, String keyword);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
