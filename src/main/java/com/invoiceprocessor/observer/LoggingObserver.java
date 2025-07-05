package com.invoiceprocessor.observer;

import com.invoiceprocessor.dto.ExtractedData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingObserver implements InvoiceProcessingObserver {
    
    @Override
    public void onProcessingStarted(String xmlType) {
        log.info("🚀 Processing started for XML type: {}", xmlType);
    }
    
    @Override
    public void onValidationCompleted(String xmlType) {
        log.info("✅ XML validation completed for type: {}", xmlType);
    }
    
    @Override
    public void onDataExtracted(ExtractedData data) {
        log.info("📊 Data extracted successfully - Type: {}, NIP: {}, P1: {}, P2: {}", 
            data.getProcessorType(), data.getNip(), data.getP1(), data.getP2());
    }
    
    @Override
    public void onProcessingCompleted(String invoiceId) {
        log.info("🎉 Processing completed successfully - Invoice ID: {}", invoiceId);
    }
    
    @Override
    public void onProcessingFailed(String error) {
        log.error("❌ Processing failed: {}", error);
    }
}