package com.ing.store_management.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BusinessException {
    public AccountDisabledException(String username) {
        super("User account '" + username + "' is disabled", HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED");
    }
}