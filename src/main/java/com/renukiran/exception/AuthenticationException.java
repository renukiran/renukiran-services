package com.renukiran.exception;

/**
 * Custom exception for authentication errors
 */
public class AuthenticationException extends RuntimeException {

    private int statusCode;

    public AuthenticationException(String message) {
        super(message);
        this.statusCode = 401;
    }

    public AuthenticationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

