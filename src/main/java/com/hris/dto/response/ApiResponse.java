package com.hris.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object errors,
        String timestamp
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now().toString());
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, null, Instant.now().toString());
    }

    public static ApiResponse<Void> error(String message, Object errors) {
        return new ApiResponse<>(false, message, null, errors, Instant.now().toString());
    }
}
