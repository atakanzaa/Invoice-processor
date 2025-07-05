package com.invoiceprocessor.exception;

import com.invoiceprocessor.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Global exception handler for centralized error handling
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle Base64 decoding exceptions
     */
    @ExceptionHandler(Base64DecodingException.class)
    public ResponseEntity<ErrorResponse> handleBase64DecodingException(
            Base64DecodingException ex, HttpServletRequest request) {
        log.error("Base64 decoding error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "BASE64_DECODING_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle XML validation exceptions
     */
    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ErrorResponse> handleXmlValidationException(
            XmlValidationException ex, HttpServletRequest request) {
        log.error("XML validation error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "XML_VALIDATION_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle XML unmarshalling exceptions
     */
    @ExceptionHandler(XmlUnmarshallingException.class)
    public ResponseEntity<ErrorResponse> handleXmlUnmarshallingException(
            XmlUnmarshallingException ex, HttpServletRequest request) {
        log.error("XML unmarshalling error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "XML_UNMARSHALLING_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle data extraction exceptions
     */
    @ExceptionHandler(DataExtractionException.class)
    public ResponseEntity<ErrorResponse> handleDataExtractionException(
            DataExtractionException ex, HttpServletRequest request) {
        log.error("Data extraction error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "DATA_EXTRACTION_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle validation errors (e.g., @Valid, @NotBlank)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Constraint violation: {}", ex.getMessage());
        
        String message = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "CONSTRAINT_VIOLATION",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Malformed JSON request: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "MALFORMED_JSON",
            "Invalid JSON format in request body",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle method argument type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Method argument type mismatch: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "ARGUMENT_TYPE_MISMATCH",
            "Invalid argument type: " + ex.getName(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle database access exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex, HttpServletRequest request) {
        log.error("Database access error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "DATABASE_ERROR",
            "Database operation failed",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle generic runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "RUNTIME_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 