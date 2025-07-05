package com.invoiceprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object for validation chain
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationContext {
    
    private String xmlContent;
    private Object xmlObject;
    private String xmlType;
    private Map<String, Object> attributes = new HashMap<>();
    
    public ValidationContext(String xmlContent) {
        this.xmlContent = xmlContent;
    }
    
    public ValidationContext(String xmlContent, Object xmlObject) {
        this.xmlContent = xmlContent;
        this.xmlObject = xmlObject;
        this.xmlType = xmlObject != null ? xmlObject.getClass().getSimpleName() : "Unknown";
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        return type.isInstance(value) ? (T) value : null;
    }
} 