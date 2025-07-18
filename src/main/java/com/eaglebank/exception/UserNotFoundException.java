package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends EagleRuntimeException {
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
    }
}