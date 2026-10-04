package com.bookstore.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BookstoreException {
    public ResourceNotFoundException(String resource, String id) {
        super(resource.toUpperCase() + "_001",
              resource + " not found with id: " + id,
              HttpStatus.NOT_FOUND);
    }
}
