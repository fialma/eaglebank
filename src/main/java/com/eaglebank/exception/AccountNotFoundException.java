package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends EagleRuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Bank account not found with account number: " + accountNumber, HttpStatus.NOT_FOUND);
    }
}