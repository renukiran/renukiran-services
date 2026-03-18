package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException{
    public ResourceNotFoundException(String message) {
        super(message, "COURSE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
