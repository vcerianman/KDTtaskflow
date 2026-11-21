package com.kdt.taskflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory cache for storing 6-digit password reset codes
 * with automatic 5-minute expiration and a 45-second resend cooldown.
 */
@Component
public class PasswordResetCodeCache {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetCodeCache.class);

    private static final long COOLDOWN_SECONDS = 45L;

    public record CacheEntry(String code, Instant expiresAt, Instant lastSentAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        long getCooldownRemainingSeconds() {
            if (lastSentAt == null) {
                return 0;
            }
            long elapsed = Duration.between(lastSentAt, Instant.now()).toSeconds();
            return elapsed < COOLDOWN_SECONDS ? (COOLDOWN_SECONDS - elapsed) : 0;
        }
    }

    private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();

    /**
     * Check if user is currently under the 45-second email cooldown.
     * Returns remaining seconds to wait, or 0 if allowed to send.
     */
    public long getCooldownRemaining(String username) {
        CacheEntry entry = store.get(username.toLowerCase());
        if (entry == null) {
            return 0;
        }
        return entry.getCooldownRemainingSeconds();
    }

    /**
     * Store verification code for a given username with a specific TTL in seconds.
     * Records the lastSentAt timestamp for 45-second rate limiting.
     */
    public void saveCode(String username, String code, long durationSeconds) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(durationSeconds);
        store.put(username.toLowerCase(), new CacheEntry(code, expiresAt, now));
        log.info("Saved password reset code for user '{}' with TTL of {}s (cooldown {}s)",
                username, durationSeconds, COOLDOWN_SECONDS);
    }

    /**
     * Retrieve the active verification code if not expired.
     */
    public Optional<String> getCode(String username) {
        CacheEntry entry = store.get(username.toLowerCase());
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            store.remove(username.toLowerCase());
            return Optional.empty();
        }
        return Optional.of(entry.code());
    }

    /**
     * Remove any stored code for the user.
     */
    public void remove(String username) {
        store.remove(username.toLowerCase());
    }

    /**
     * Periodic cleanup of expired entries (runs every 60 seconds).
     */
    @Scheduled(fixedDelay = 60000)
    public void cleanExpiredEntries() {
        int initialSize = store.size();
        store.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removed = initialSize - store.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired password reset codes from cache", removed);
        }
    }
}
