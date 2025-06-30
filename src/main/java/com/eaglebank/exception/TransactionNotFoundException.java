package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends EagleRuntimeException {
    public TransactionNotFoundException(String transactionId, String accountNumber) {
        super("Transaction not found with ID: " + transactionId + " for account: " + accountNumber, HttpStatus.NOT_FOUND);
    }
}