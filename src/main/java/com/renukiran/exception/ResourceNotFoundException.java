package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException{
    public ResourceNotFoundException(String message, String error_code) {
        super(message, error_code, HttpStatus.NOT_FOUND);
    }
}
