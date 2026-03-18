package com.renukiran.exception;

import com.renukiran.dto.SignInResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SignInResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .toList();

        SignInResponse response = new SignInResponse(
                false,
                "Validation failed",
                errors,   // now sending list instead of single string
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

