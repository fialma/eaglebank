package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
public class WrongCredentialsException extends EagleRuntimeException {
    public WrongCredentialsException() {
        super("", HttpStatus.UNAUTHORIZED);
    }
}