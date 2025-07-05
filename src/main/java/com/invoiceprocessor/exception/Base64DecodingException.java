package com.invoiceprocessor.exception;

/**
 * Exception thrown when Base64 decoding fails
 */
public class Base64DecodingException extends RuntimeException {
    
    public Base64DecodingException(String message) {
        super(message);
    }
    
    public Base64DecodingException(String message, Throwable cause) {
        super(message, cause);
    }
} 