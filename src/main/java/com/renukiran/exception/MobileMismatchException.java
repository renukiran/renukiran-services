package com.renukiran.exception;

import org.springframework.http.HttpStatus;

public class MobileMismatchException extends ApplicationException{
    public MobileMismatchException(String message) {
        super(message, "MOBILE_MISMATCH", HttpStatus.BAD_REQUEST);
    }
}
