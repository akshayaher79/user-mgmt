package com.example.user_mgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;

import jakarta.persistence.PersistenceException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFound(NoSuchElementException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFound(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Data integrity violation: duplicate or invalid data");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content type not supported");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed");
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Object> handleDbError(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception ex) {
        return build(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("Validation failed");
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
    }

    private ResponseEntity<Object> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
