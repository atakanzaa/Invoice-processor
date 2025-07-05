package com.invoiceprocessor.chain;

import com.invoiceprocessor.dto.ValidationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Chain of Responsibility Pattern for validation pipeline
 */
@Slf4j
public abstract class ValidationHandler {
    
    protected ValidationHandler nextHandler;
    
    public ValidationHandler setNext(ValidationHandler handler) {
        this.nextHandler = handler;
        return handler;
    }
    
    public final void handle(ValidationContext context) {
        try {
            if (canHandle(context)) {
                log.debug("🔍 {} handling validation", getHandlerName());
                doValidation(context);
                log.debug("✅ {} validation passed", getHandlerName());
            }
            
            if (nextHandler != null) {
                nextHandler.handle(context);
            }
            
        } catch (Exception e) {
            log.error("❌ {} validation failed: {}", getHandlerName(), e.getMessage());
            throw e;
        }
    }
    
    protected abstract boolean canHandle(ValidationContext context);
    protected abstract void doValidation(ValidationContext context);
    protected abstract String getHandlerName();
} 