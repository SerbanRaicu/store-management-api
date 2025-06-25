package com.ing.store_management.exception;

import org.springframework.http.HttpStatus;

public class DuplicateProductException extends BusinessException {
    public DuplicateProductException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_PRODUCT");
    }
}
