package com.ordershopx.backend.shared.exception;

import com.ordershopx.backend.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId() {
        return MDC.get("traceId");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(e ->
                errors.put(e.getField(), e.getDefaultMessage())
        );

        log.warn("traceId={} event=api_error_validation method={} path={} errors={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(
                        "Error de validación",
                        errors,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "VAL_400"
                )
        );
    }

    // JSON MAL FORMADO
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_invalid_json method={} path={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(
                        "JSON inválido o mal formado",
                        null,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "JSON_400"
                )
        );
    }

    // TYPE MISMATCH (UUID, etc.)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_type_mismatch method={} path={} message={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(
                        "Parámetro inválido en la URL",
                        null,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "TYPE_400"
                )
        );
    }

    // BAD REQUEST
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_bad_request method={} path={} message={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "BR_400"
                )
        );
    }

    // CONFLICT (duplicados)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_conflict method={} path={} message={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.CONFLICT.value(),
                        request.getRequestURI(),
                        "CONFLICT_409"
                )
        );
    }

    // NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_not_found method={} path={} message={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI(),
                        "NOT_FOUND_404"
                )
        );
    }

    // ARGUMENTOS INVÁLIDOS
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("traceId={} event=api_error_illegal_argument method={} path={} message={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "ARG_400"
                )
        );
    }

    // ERROR GENERAL (CRÍTICO)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error("traceId={} event=api_error_internal method={} path={}",
                traceId(),
                request.getMethod(),
                request.getRequestURI(),
                ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error(
                        "Error interno del servidor",
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        request.getRequestURI(),
                        "INT_500"
                )
        );
    }
}