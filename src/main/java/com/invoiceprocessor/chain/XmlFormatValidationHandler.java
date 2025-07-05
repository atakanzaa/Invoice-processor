package com.invoiceprocessor.chain;

import com.invoiceprocessor.dto.ValidationContext;
import com.invoiceprocessor.exception.XmlValidationException;
import org.springframework.stereotype.Component;

/**
 * Validates basic XML format
 */
@Component
public class XmlFormatValidationHandler extends ValidationHandler {
    
    @Override
    protected boolean canHandle(ValidationContext context) {
        return context.getXmlContent() != null;
    }
    
    @Override
    protected void doValidation(ValidationContext context) {
        String xmlContent = context.getXmlContent();
        
        // Basic XML format checks
        if (!xmlContent.trim().startsWith("<?xml")) {
            throw new XmlValidationException("XML must start with XML declaration");
        }
        
        if (!xmlContent.contains("<") || !xmlContent.contains(">")) {
            throw new XmlValidationException("Invalid XML format: missing angle brackets");
        }
        
        // Check for balanced tags (basic check)
        long openTags = xmlContent.chars().filter(ch -> ch == '<').count();
        long closeTags = xmlContent.chars().filter(ch -> ch == '>').count();
        
        if (openTags != closeTags) {
            throw new XmlValidationException("Invalid XML format: unbalanced tags");
        }
    }
    
    @Override
    protected String getHandlerName() {
        return "XMLFormat";
    }
} 