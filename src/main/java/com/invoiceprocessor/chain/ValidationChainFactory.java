package com.invoiceprocessor.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory for creating validation chain
 */
@Component
@RequiredArgsConstructor
public class ValidationChainFactory {
    
    private final XmlFormatValidationHandler xmlFormatHandler;
    private final XmlSchemaValidationHandler xmlSchemaHandler;
    
    /**
     * Create the complete validation chain
     */
    public ValidationHandler createValidationChain() {
        // Build chain: Format -> Schema
        xmlFormatHandler.setNext(xmlSchemaHandler);
        
        return xmlFormatHandler;
    }
    
    /**
     * Create validation chain for specific type
     */
    public ValidationHandler createValidationChain(String xmlType) {
        ValidationHandler chain = createValidationChain();
        
        // Can add type-specific handlers here in the future
        // Example: if ("Faktura".equals(xmlType)) { ... }
        
        return chain;
    }
} 