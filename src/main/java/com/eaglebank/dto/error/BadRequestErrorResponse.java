package com.eaglebank.dto.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestErrorResponse extends ErrorResponse {
    private List<ErrorDetail> details;

    public BadRequestErrorResponse(String message, List<ErrorDetail> details) {
        super(message);
        this.details = details;
    }
}