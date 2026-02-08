package com.example.moment_forever.common.errorhandler;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1️⃣ Handle DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.buildValidationErrorResponse("validation failed",errors));
    }

    // 2️⃣ Handle Business Validation logic exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.buildBadRequestResponse(ex.getMessage()));
    }

    // 3️⃣ Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.info("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    // 4️⃣ Handle other runtime exceptions
    @ExceptionHandler(CustomAuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomAuthException(RuntimeException ex) {
        log.error("Unexpected auth exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    // 5️⃣ Optional: Catch-all for any other exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}