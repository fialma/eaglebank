package com.eaglebank.config;

import com.eaglebank.dto.error.BadRequestErrorResponse;
import com.eaglebank.dto.error.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<BadRequestErrorResponse> handle(MethodArgumentNotValidException exception) {
        //you will get all javax failed validation, can be more than one
        //so you can return the set of error messages or just the first message


        List<ErrorDetail> errorDetails = exception.getFieldErrors().stream()
                .map(violation -> new ErrorDetail(
                        violation.getField(),
                        violation.getDefaultMessage(),
                        violation.getCode()
                ))
                .collect(Collectors.toList());
        var errorResponse = new BadRequestErrorResponse("Validation Error.", errorDetails);
        return new ResponseEntity<BadRequestErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}