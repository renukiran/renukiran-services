package com.renukiran.exception;

import com.renukiran.dto.ErrorResponse;
import com.renukiran.dto.SignInResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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



        // Log the exception so it is visible in the console/log files
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                List.of("Something went wrong")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleAppException(ApplicationException ex) {


        // Log application exceptions for debugging
        log.error("ApplicationException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                ex.getHttpStatus().value(),
                ex.getErrorCode(),
               List.of(ex.getMessage())
        );

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}
