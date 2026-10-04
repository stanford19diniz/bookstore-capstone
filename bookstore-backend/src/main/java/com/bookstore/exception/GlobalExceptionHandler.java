package com.bookstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler — converts all exceptions to RFC 7807 Problem Details.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex,
                                                         HttpServletRequest req) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex,
                                                         HttpServletRequest req) {
        return buildProblem(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRule(BusinessRuleException ex,
                                                             HttpServletRequest req) {
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BookstoreException.class)
    public ResponseEntity<ProblemDetail> handleBookstoreException(BookstoreException ex,
                                                                   HttpServletRequest req) {
        return buildProblem(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest req) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map((FieldError fe) -> Map.of("field", fe.getField(),
                                               "message", fe.getDefaultMessage() != null
                                                           ? fe.getDefaultMessage() : "Invalid value"))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("about:blank"));
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields failed validation");
        problem.setInstance(URI.create(req.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException ex,
                                                               HttpServletRequest req) {
        return buildProblem(HttpStatus.UNAUTHORIZED, "AUTH_004", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex,
                                                             HttpServletRequest req) {
        return buildProblem(HttpStatus.FORBIDDEN, "AUTH_005", "Access denied", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001",
                            "An unexpected error occurred", req.getRequestURI());
    }

    private ResponseEntity<ProblemDetail> buildProblem(HttpStatus status, String code,
                                                        String detail, String instance) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(status.getReasonPhrase());
        problem.setDetail(detail);
        problem.setInstance(URI.create(instance));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errorCode", code);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
