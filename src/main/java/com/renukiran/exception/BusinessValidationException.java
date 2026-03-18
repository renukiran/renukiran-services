package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class BusinessValidationException extends ApplicationException{
    public BusinessValidationException(String message) {
        super(message, "SLOT_CONFLICT", HttpStatus.CONFLICT);
    }
}
