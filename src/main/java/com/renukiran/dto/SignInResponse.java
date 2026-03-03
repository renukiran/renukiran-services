package com.renukiran.dto;

/**
 * DTO for trainee sign-in response
 */
public class SignInResponse {

    private boolean success;
    private String message;
    private String token;
    private int statusCode;

    public SignInResponse() {
    }

    public SignInResponse(boolean success, String message, String token, int statusCode) {
        this.success = success;
        this.message = message;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

