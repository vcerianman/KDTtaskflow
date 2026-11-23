package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.NotificationResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationSseService {

    /**
     * Subscribe a user to their real-time notification SSE stream.
     *
     * @param userId   the recipient's user ID.
     * @param username the recipient's username.
     * @return the configured SseEmitter.
     */
    SseEmitter subscribe(Long userId, String username);

    /**
     * Push a notification to the recipient if they are currently connected via SSE.
     */
    void sendNotification(Long recipientId, NotificationResponse notification);

    /**
     * Push notifications to multiple recipients.
     */
    void sendNotifications(NotificationResponse notification);

    /**
     * Current active notification stream subscriber count.
     */
    int getActiveSubscriberCount();
}

