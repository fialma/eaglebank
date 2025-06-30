package com.eaglebank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EagleRuntimeException extends RuntimeException{

    private final HttpStatus httpStatus;
    public EagleRuntimeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
