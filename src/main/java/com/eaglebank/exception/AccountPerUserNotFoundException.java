package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountPerUserNotFoundException extends EagleRuntimeException {
    public AccountPerUserNotFoundException(String accountNumber, String userId) {
        super("Bank account not found with account number: " + accountNumber + " for user: " + userId, HttpStatus.NOT_FOUND);
    }
}