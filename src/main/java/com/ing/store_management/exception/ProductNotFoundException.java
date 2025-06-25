package com.ing.store_management.exception;

import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }
}
