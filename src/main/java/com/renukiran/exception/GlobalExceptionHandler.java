package com.renukiran.exception;

import com.renukiran.dto.ErrorResponse;
import com.renukiran.dto.SignInResponse;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                List.of("Something went wrong")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleAppException(ApplicationException ex) {

        ErrorResponse response = new ErrorResponse(
                ex.getHttpStatus().value(),
                ex.getErrorCode(),
               List.of(ex.getMessage())
        );

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}

