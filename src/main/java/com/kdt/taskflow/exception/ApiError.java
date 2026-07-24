package com.kdt.taskflow.exception;

import java.time.OffsetDateTime;
import java.util.Map;

/**Cấu trúc JSON lỗi thống nhất cho toàn API.*/
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, null);
    }

    public static ApiError validation(String message, String path, Map<String, String> fieldErrors) {
        return new ApiError(OffsetDateTime.now(), 400, "Bad Request", message, path, fieldErrors);
    }
}