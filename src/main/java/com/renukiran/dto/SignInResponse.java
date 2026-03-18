package com.renukiran.dto;

import java.util.List;

/**
 * DTO for trainee sign-in response
 */
public class SignInResponse {

    private boolean success;
    private String message;
    private List<String> errors;
    private int statusCode;

    public SignInResponse() {
    }

    public SignInResponse(boolean success, String message, List<String> errors, int statusCode) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

