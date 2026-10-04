package com.bookstore.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all bookstore business exceptions.
 * Maps to RFC 7807 Problem Details responses via GlobalExceptionHandler.
 */
public class BookstoreException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public BookstoreException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() { return errorCode; }
    public HttpStatus getStatus() { return status; }
}
