package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class UserHasAccountsException extends EagleRuntimeException {
    public UserHasAccountsException() {
        super("User has associated account(s) and cannot be deleted.", HttpStatus.CONFLICT);
    }
}