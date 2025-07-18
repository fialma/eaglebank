package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
public class ForbiddenException extends EagleRuntimeException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}