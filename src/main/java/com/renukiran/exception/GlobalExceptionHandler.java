package com.renukiran.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.renukiran.dto.ApiErrorResponse;
import com.renukiran.dto.ErrorResponse;
import com.renukiran.dto.SignInResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(HttpMessageNotReadableException ex) {

        String errorMessage = "Invalid request format";

        if (ex.getCause() instanceof InvalidFormatException ife) {

            String fieldName = ife.getPath()
                    .stream()
                    .map(ref -> ref.getFieldName())
                    .collect(Collectors.joining("."));

            if (ife.getTargetType().equals(LocalDate.class)) {
                errorMessage = fieldName + " must be in format yyyy-MM-dd";
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse( HttpStatus.BAD_REQUEST.value(),"INVALID_FORMAT", List.of(errorMessage)));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiErrorResponse<Object> response = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_FAILED")
                .message("Validation failed")
                .details(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleGeneric(Exception ex) {


        // Log the exception so it is visible in the console/log files
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        ApiErrorResponse<Object> response = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_ERROR")
                .message("Something went wrong")
                .details(List.of(ex.getMessage()))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleAppException(ApplicationException ex) {

        // Log application exceptions for debugging
        log.error("ApplicationException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

        ApiErrorResponse<Object> response = ApiErrorResponse.builder()
                .status(ex.getHttpStatus().value())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .details(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}
