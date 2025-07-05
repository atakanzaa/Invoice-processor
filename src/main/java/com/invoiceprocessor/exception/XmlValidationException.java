package com.invoiceprocessor.exception;

/**
 * Exception thrown when XML validation against XSD fails
 */
public class XmlValidationException extends RuntimeException {
    
    public XmlValidationException(String message) {
        super(message);
    }
    
    public XmlValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 