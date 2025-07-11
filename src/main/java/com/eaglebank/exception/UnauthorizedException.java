package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
public class UnauthorizedException extends EagleRuntimeException {
    public UnauthorizedException() {
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}