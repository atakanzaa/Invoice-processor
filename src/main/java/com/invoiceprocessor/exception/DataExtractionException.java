package com.invoiceprocessor.exception;

/**
 * Exception thrown when data extraction from XML fails
 */
public class DataExtractionException extends RuntimeException {
    
    public DataExtractionException(String message) {
        super(message);
    }
    
    public DataExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
} 