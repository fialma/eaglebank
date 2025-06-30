package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserDetailNotAllowedException extends EagleRuntimeException {
    public UserDetailNotAllowedException() {
        super("You are not allowed to access this user's details.", HttpStatus.FORBIDDEN);
    }
}