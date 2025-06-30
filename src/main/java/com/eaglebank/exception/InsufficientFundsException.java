package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422 Unprocessable Entity
public class InsufficientFundsException extends EagleRuntimeException {
    public InsufficientFundsException(BigDecimal balance) {
        super("Insufficient funds for withdrawal. Current balance: " + balance.doubleValue(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}