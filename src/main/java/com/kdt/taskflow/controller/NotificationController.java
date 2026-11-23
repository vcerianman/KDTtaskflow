package com.kdt.taskflow.controller;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.dto.NotificationCreateRequest;
import com.kdt.taskflow.dto.NotificationDeleteRequest;
import com.kdt.taskflow.dto.NotificationResponse;
import com.kdt.taskflow.dto.NotificationUpdateRequest;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.NotificationService;
import com.kdt.taskflow.service.NotificationSseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;
    private final UserMapper userMapper;

    public NotificationController(NotificationService notificationService,
                                  NotificationSseService notificationSseService,
                                  UserMapper userMapper) {
        this.notificationService = notificationService;
        this.notificationSseService = notificationSseService;
        this.userMapper = userMapper;
    }

    /**
     * GET /api/notifications/{userId}
     * Fetch a user's notifications (ordered newest first).
     * Optional query parameters:
     * - ?limit=20
     * - ?unreadOnly=true
     */
    @GetMapping("/{userId:[0-9]+}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Boolean unreadOnly) {
        List<NotificationResponse> list = notificationService.getByUserId(userId, limit, unreadOnly);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/notifications/n/{userId}
     * Return the count of unread (is_read = false) notifications to display on the NavBar bell badge.
     */
    @GetMapping("/n/{userId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.countUnread(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("unreadCount", count);
        body.put("count", count);
        return ResponseEntity.ok(body);
    }

    /**
     * GET /api/notifications/stream
     * Opens a Server-Sent Events (SSE) connection (using ?token=...) to push new notifications
     * to the user's browser immediately.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal String currentUsername) {

        Long effectiveUserId = userId;

        // If userId is not explicitly provided in query, resolve from authenticated JWT username
        if (effectiveUserId == null && currentUsername != null && !currentUsername.isBlank()) {
            effectiveUserId = userMapper.findByUsername(currentUsername)
                    .map(User::getId)
                    .orElse(null);
        }

        return notificationSseService.subscribe(effectiveUserId, currentUsername);
    }

    /**
     * POST /api/notifications
     * Create notifications for one or multiple recipients:
     * {
     *   "recipientIds": [3, 2, 4, 7, 9],
     *   "actorId": 1,
     *   "type": "...",
     *   "message": "@KDT rearranged tasks in Project A",
     *   "link": "/tasks?projectId=1"
     * }
     */
    @PostMapping
    public ResponseEntity<List<NotificationResponse>> create(
            @Valid @RequestBody NotificationCreateRequest request) {
        List<NotificationResponse> created = notificationService.createNotifications(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PATCH or PUT /api/notifications
     * Batch update notifications (supports multiple IDs and/or by recipient userId).
     * Supports "half requests" — null fields are ignored and untouched.
     * When is_read is updated to true, seen_at is automatically recorded as NOW().
     */
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Map<String, Object>> updateBatch(
            @RequestBody NotificationUpdateRequest request) {
        int affected = notificationService.updateNotifications(null, request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("updatedRows", affected);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH or PUT /api/notifications/{id}
     * Update a single notification by ID.
     */
    @RequestMapping(value = "/{id:[0-9]+}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Map<String, Object>> updateSingle(
            @PathVariable Long id,
            @RequestBody NotificationUpdateRequest request) {
        int affected = notificationService.updateNotifications(id, request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("updatedRows", affected);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/notifications
     * Batch delete notifications (by notificationIds array and/or userId).
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteBatch(
            @RequestBody(required = false) NotificationDeleteRequest request,
            @RequestParam(required = false) Long userId) {
        int affected = notificationService.deleteNotifications(null, request, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("deletedRows", affected);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/notifications/{id}
     * Delete a single notification by ID.
     */
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Map<String, Object>> deleteSingle(@PathVariable Long id) {
        int affected = notificationService.deleteNotifications(id, null, null);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("deletedRows", affected);
        return ResponseEntity.ok(response);
    }
}

