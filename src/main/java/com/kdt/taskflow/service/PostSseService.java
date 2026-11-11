package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.PostEvent;
import com.kdt.taskflow.dto.PostResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface PostSseService {

    /**
     * Subscribe a client to the post communication SSE stream.
     *
     * @param projectId optional project ID filter. If null, the client receives events for all projects.
     * @param username  the username of the authenticated subscriber (for logging/identification).
     * @return the configured SseEmitter instance.
     */
    SseEmitter subscribe(Long projectId, String username);

    /**
     * Broadcast a generic post event to matching subscribers.
     */
    void broadcast(PostEvent event);

    /**
     * Broadcast a new post or reply creation event.
     */
    void broadcastCreated(PostResponse post);

    /**
     * Broadcast a post or reply update event.
     */
    void broadcastUpdated(PostResponse post);

    /**
     * Broadcast a post or reply deletion event.
     */
    void broadcastDeleted(Long postId, Long projectId);

    /**
     * Get the count of currently active SSE subscribers.
     */
    int getActiveSubscriberCount();
}

