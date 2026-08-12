package com.kdt.taskflow.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.ResetPasswordRequest;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isCallerGod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GOD"));
    }

    private void checkGodAccess(User targetUser, UserRole targetRole) {
        boolean callerIsGod = isCallerGod();

        if (targetUser != null && targetUser.getRole() == UserRole.GOD && !callerIsGod) {
            throw new IllegalArgumentException("Admins cannot modify GOD profiles");
        }

        if (targetRole == UserRole.GOD && !callerIsGod) {
            throw new IllegalArgumentException("Admins cannot assign the GOD role");
        }
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

        UserRole targetRole = request.role() != null ? request.role() : UserRole.MEMBER;
        checkGodAccess(null, targetRole);

        User user = request.toDomain();
        user.setRole(targetRole);
        user.setPassword(passwordEncoder.encode(request.password()));
        userMapper.insert(user);
        return getById(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));
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
    @Transactional(readOnly = true)
    public List<UserResponse> searchAdmin(String username, String email, UserRole role, UserStatus status) {
        return userMapper.searchAdmin(username, email, role, status)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        UserRole targetRole = request.role() != null ? request.role() : existing.getRole();
        checkGodAccess(existing, targetRole);

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
        user.setRole(targetRole);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        } else {
            user.setPassword(existing.getPassword());
        }

        int affected = userMapper.update(user);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find user id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        checkGodAccess(existing, null);

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        int affected = userMapper.updatePassword(id, encodedPassword);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find user id=" + id);
        }
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        checkGodAccess(existing, null);

        int affected = userMapper.softDeleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find user id=" + id);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        checkGodAccess(existing, null);

        int affected = userMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find user id=" + id);
        }
    }
}
