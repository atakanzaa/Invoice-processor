package com.invoiceprocessor.exception;

/**
 * Exception thrown when XML unmarshalling fails
 */
public class XmlUnmarshallingException extends RuntimeException {
    
    public XmlUnmarshallingException(String message) {
        super(message);
    }
    
    public XmlUnmarshallingException(String message, Throwable cause) {
        super(message, cause);
    }
} 