package com.ing.store_management.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException{
    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }

    public InvalidCredentialsException() {
        super("Invalid username or password", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}
