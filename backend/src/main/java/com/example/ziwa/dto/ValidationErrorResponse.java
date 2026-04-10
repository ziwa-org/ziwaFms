package com.example.ziwa.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {
    private List<FieldError> errors;

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(String code, String message, List<FieldError> errors, LocalDateTime timestamp) {
        super(code, message, timestamp);
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }
}
