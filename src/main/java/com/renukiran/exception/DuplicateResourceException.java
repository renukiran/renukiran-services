package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApplicationException{
    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_COURSE", HttpStatus.CONFLICT);
    }
}
