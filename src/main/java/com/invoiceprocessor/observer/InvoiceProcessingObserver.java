package com.invoiceprocessor.observer;

import com.invoiceprocessor.dto.ExtractedData;

/**
 * Observer interface for monitoring invoice processing events
 */
public interface InvoiceProcessingObserver {
    
    /**
     * Called when processing starts
     */
    void onProcessingStarted(String xmlType);
    
    /**
     * Called when XML validation is completed
     */
    void onValidationCompleted(String xmlType);
    
    /**
     * Called when data extraction is completed
     */
    void onDataExtracted(ExtractedData data);
    
    /**
     * Called when processing is completed successfully
     */
    void onProcessingCompleted(String invoiceId);
    
    /**
     * Called when processing fails
     */
    void onProcessingFailed(String error);
}