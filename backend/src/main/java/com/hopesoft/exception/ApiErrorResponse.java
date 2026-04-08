package com.hopesoft.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {

    public static ApiErrorResponse of(HttpStatus status, String code, String message, String path) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                path
        );
    }
}
