package com.kdt.taskflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        if (userMapper.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        if (userMapper.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        User user = request.toDomain();
        userMapper.insert(user);
        return getById(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> search(UserRole role, String keyword) {
        return userMapper.search(role, keyword)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));

        userMapper.findByUsername(request.username()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new IllegalArgumentException("Username already exists: " + request.username());
            }
        });

        userMapper.findByEmail(request.email()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + request.email());
            }
        });

        User user = request.toDomain();
        user.setId(id);

        int affected = userMapper.update(user);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy user id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = userMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy user id=" + id);
        }
    }
}
