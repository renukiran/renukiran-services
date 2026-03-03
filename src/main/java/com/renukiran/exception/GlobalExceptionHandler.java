package com.renukiran.exception;

import com.renukiran.dto.SignInResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<SignInResponse> handleAuthenticationException(
            AuthenticationException ex) {

        SignInResponse response = new SignInResponse(
            false,
            ex.getMessage(),
            null,
            ex.getStatusCode()
        );

        return ResponseEntity
            .status(ex.getStatusCode())
            .body(response);
    }

    /**
     * Handle validation exceptions (invalid request body)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SignInResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        SignInResponse response = new SignInResponse(
            false,
            errorMessage.toString(),
            null,
            400
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SignInResponse> handleGenericException(Exception ex) {

        SignInResponse response = new SignInResponse(
            false,
            "An error occurred: " + ex.getMessage(),
            null,
            500
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}

