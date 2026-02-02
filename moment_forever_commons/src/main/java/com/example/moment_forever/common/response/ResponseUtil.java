package com.example.moment_forever.common.response;

import org.springframework.http.HttpStatus;

public class ResponseUtil {

    private ResponseUtil() {
        // Utility class - prevent instantiation
    }

    public static <T> ApiResponse<T> buildOkResponse(T data, String message) {
        return ApiResponse.<T>builder()
                .setCode(HttpStatus.OK.value())
                .setStatus("SUCCESS")
                .setMsg(message)
                .setResponse(data)
                .build();
    }

    public static <T> ApiResponse<T> buildCreatedResponse(T data, String message) {
        return ApiResponse.<T>builder()
                .setCode(HttpStatus.CREATED.value())
                .setStatus("SUCCESS")
                .setMsg(message)
                .setResponse(data)
                .build();
    }

    public static <T> ApiResponse<T> buildErrorResponse(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .setCode(status.value())
                .setStatus("INTERNAL_SERVER_ERROR")
                .setMsg(message)
                .setResponse(null)
                .build();
    }

    public static <T> ApiResponse<T> buildConflictResponse(String message) {
        return buildErrorResponse(message, HttpStatus.CONFLICT);
    }

    public static <T> ApiResponse<T> buildNotFoundResponse(String message) {
        return buildErrorResponse(message, HttpStatus.NOT_FOUND);
    }

    public static <T> ApiResponse<T> buildBadRequestResponse(String message) {
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

}