package com.ing.store_management.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_USER");
    }
}
