package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class UserWithSameEmailAlreadyExistException extends EagleRuntimeException {
    public UserWithSameEmailAlreadyExistException() {
        super("User with this email already exists.", HttpStatus.CONFLICT);
    }
}