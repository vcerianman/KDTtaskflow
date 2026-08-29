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
import com.kdt.taskflow.dto.UserUpdateRequest;
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
    public List<UserResponse> search(Long projectId, UserRole role, String keyword) {
        return userMapper.search(projectId, role, keyword)
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
    public UserResponse update(Long id, String currentUsername, UserUpdateRequest request) {
        User loggedInUser = userMapper.findByUsername(currentUsername)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Unauthorized user"));

        boolean isSelf = loggedInUser.getId().equals(id);

        if (!isSelf) {
            // When updating another user, ONLY the projects field can be modified
            if ((request.email() != null && !request.email().isBlank()) ||
                (request.fullName() != null && !request.fullName().isBlank()) ||
                (request.about() != null && !request.about().isEmpty()) ||
                request.status() != null ||
                (request.password() != null && !request.password().isBlank())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Forbidden: You are only authorized to update projects for other users");
            }
        }

        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        // 1. If email is provided and not empty, check uniqueness and update (only for self)
        if (isSelf && request.email() != null && !request.email().isBlank()) {
            userMapper.findByEmail(request.email().trim()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already exists: " + request.email());
                }
            });
            existing.setEmail(request.email().trim());
        }

        // 2. If fullName is provided and not empty, update (only for self)
        if (isSelf && request.fullName() != null && !request.fullName().isBlank()) {
            existing.setFullName(request.fullName().trim());
        }

        // 3. If about is provided, update (only for self)
        if (isSelf && request.about() != null) {
            existing.setAbout(request.about());
        }

        // 4. If projects is provided, update (allowed for any user!)
        if (request.projects() != null) {
            existing.setProjects(request.projects());
        }

        // 5. If status is provided, update (only for self)
        if (isSelf && request.status() != null) {
            existing.setStatus(request.status());
        }

        // 6. If password is provided and not empty, hash/encode and overwrite (only for self)
        if (isSelf && request.password() != null && !request.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.password()));
        }

        int affected = userMapper.update(existing);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find user id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public UserResponse updateAdmin(Long id, UserRequest request) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user id=" + id));

        UserRole targetRole = request.role() != null ? request.role() : existing.getRole();
        checkGodAccess(existing, targetRole);

        if (request.username() != null && !request.username().isBlank()) {
            userMapper.findByUsername(request.username().trim()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("Username already exists: " + request.username());
                }
            });
            existing.setUsername(request.username().trim());
        }

        if (request.email() != null && !request.email().isBlank()) {
            userMapper.findByEmail(request.email().trim()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already exists: " + request.email());
                }
            });
            existing.setEmail(request.email().trim());
        }

        if (request.fullName() != null && !request.fullName().isBlank()) {
            existing.setFullName(request.fullName().trim());
        }

        if (request.about() != null) {
            existing.setAbout(request.about());
        }

        if (request.projects() != null) {
            existing.setProjects(request.projects());
        }

        existing.setRole(targetRole);
        if (request.status() != null) {
            existing.setStatus(request.status());
        }

        if (request.password() != null && !request.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.password()));
        }

        int affected = userMapper.update(existing);
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
