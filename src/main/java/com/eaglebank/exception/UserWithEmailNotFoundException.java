package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserWithEmailNotFoundException extends EagleRuntimeException {
    public UserWithEmailNotFoundException(String userId) {
        super("User not found with email: " + userId, HttpStatus.NOT_FOUND);
    }
}