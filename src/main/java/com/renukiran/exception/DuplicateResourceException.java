package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApplicationException{
    public DuplicateResourceException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.CONFLICT);
    }
}
