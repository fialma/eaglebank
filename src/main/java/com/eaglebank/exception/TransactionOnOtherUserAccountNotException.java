package com.eaglebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionOnOtherUserAccountNotException extends EagleRuntimeException {
    public TransactionOnOtherUserAccountNotException() {
        super("You are not allowed to perform operations on other users' accounts.", HttpStatus.FORBIDDEN);
    }
}