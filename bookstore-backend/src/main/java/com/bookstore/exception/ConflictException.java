package com.bookstore.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BookstoreException {
    public ConflictException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}
