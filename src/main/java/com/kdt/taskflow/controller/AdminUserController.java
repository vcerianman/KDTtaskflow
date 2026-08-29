package com.kdt.taskflow.controller;

import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.ResetPasswordRequest;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/admin/users — List users, findable by username/email, sortable/filterable by role and status.
     */
    @GetMapping
    public List<UserResponse> search(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String email,
                                     @RequestParam(required = false) UserRole role,
                                     @RequestParam(required = false) UserStatus status) {
        return userService.searchAdmin(username, email, role, status);
    }

    /**
     * GET /api/admin/users/{id} — Detailed User info.
     */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * POST /api/admin/users — Create a new user account.
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request,
                                               UriComponentsBuilder uriBuilder) {
        UserResponse created = userService.create(request);
        URI location = uriBuilder.path("/api/admin/users/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/admin/users/{id} — Update user account.
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UserRequest request) {
        return userService.updateAdmin(id, request);
    }

    /**
     * POST /api/admin/users/{id}/reset-password — Reset password for user.
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
                                              @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/admin/users/{id} — Soft delete user (204 No Content).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.softDelete(id);
    }
}
