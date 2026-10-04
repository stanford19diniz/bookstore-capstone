package com.bookstore.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends BookstoreException {
    public BusinessRuleException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
