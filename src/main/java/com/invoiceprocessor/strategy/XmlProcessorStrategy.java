package com.invoiceprocessor.strategy;

import com.invoiceprocessor.dto.ExtractedData;

/**
 * Strategy interface for processing different types of XML documents
 */
public interface XmlProcessorStrategy {
    
    /**
     * Check if this strategy can handle the given XML object
     */
    boolean canHandle(Object xmlObject);
    
    /**
     * Extract data from the XML object
     */
    ExtractedData extractData(Object xmlObject);
    
    /**
     * Get the name of this strategy
     */
    String getStrategyName();
    
    /**
     * Get the priority of this strategy (higher number = higher priority)
     */
    int getPriority();
}