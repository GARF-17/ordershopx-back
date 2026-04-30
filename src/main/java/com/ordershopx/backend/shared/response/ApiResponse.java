package com.ordershopx.backend.shared.response;

import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    private Object errors;
    private LocalDateTime timestamp;
    private String traceId;
    private String path;
    private String errorCode;

    private static String getTraceId() {
        return MDC.get("traceId");
    }

    // RESPUESTA 200 OK
    public static <T> ApiResponse<T> success(T data, String message) {
        return buildSuccess(data, message, 200, null);
    }

    // RESPUESTA 201 CREATED
    public static <T> ApiResponse<T> created(T data, String message) {
        return buildSuccess(data, message, 201, null);
    }

    public static <T> ApiResponse<T> success(T data, String message, int status, String path) {
        return buildSuccess(data, message, status, path);
    }

    private static <T> ApiResponse<T> buildSuccess(T data, String message, int status, String path) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .errors(null)
                .timestamp(LocalDateTime.now())
                .traceId(getTraceId())
                .path(path)
                .errorCode(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status, String path) {
        return buildError(message, null, status, path, null);
    }

    public static <T> ApiResponse<T> error(String message, Object errors, int status, String path) {
        return buildError(message, errors, status, path, null);
    }

    public static <T> ApiResponse<T> error(String message, Object errors, int status, String path, String errorCode) {
        return buildError(message, errors, status, path, errorCode);
    }

    private static <T> ApiResponse<T> buildError(String message,
                                                 Object errors,
                                                 int status,
                                                 String path,
                                                 String errorCode) {

        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .traceId(getTraceId())
                .path(path)
                .errorCode(errorCode)
                .build();
    }
}