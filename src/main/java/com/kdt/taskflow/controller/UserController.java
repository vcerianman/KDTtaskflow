package com.kdt.taskflow.controller;

import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.dto.UserUpdateRequest;
import com.kdt.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** POST /api/users — Tạo user mới. Trả 201 Created + header Location. */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request,
            UriComponentsBuilder uriBuilder) {
        UserResponse created = userService.create(request);
        URI location = uriBuilder.path("/api/users/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/users — list fof users
     */
    @GetMapping
    public List<UserResponse> search(@RequestParam(required = false) Long projectId,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String keyword) {
        return userService.search(projectId, role, keyword);
    }

    /** GET /api/users/{id} — Lấy chi tiết 1 user. */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * PUT /api/users/{id} — Update user profile (fullname, email, status, password,
     * about, projects).
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
            @AuthenticationPrincipal String currentUsername,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, currentUsername, request);
    }

    /** DELETE /api/users/{id} — Xóa user theo id. Trả 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
