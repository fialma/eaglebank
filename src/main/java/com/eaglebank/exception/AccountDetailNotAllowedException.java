package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountDetailNotAllowedException extends EagleRuntimeException {
    public AccountDetailNotAllowedException() {
        super("You are not allowed to access this account's details.", HttpStatus.FORBIDDEN);
    }
}