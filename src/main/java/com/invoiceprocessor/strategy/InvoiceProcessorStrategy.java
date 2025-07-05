package com.invoiceprocessor.strategy;

import com.example.generated.InvoiceComplexType;
import com.invoiceprocessor.dto.ExtractedData;
import com.invoiceprocessor.exception.DataExtractionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InvoiceProcessorStrategy implements XmlProcessorStrategy {
    
    @Override
    public boolean canHandle(Object xmlObject) {
        return xmlObject instanceof InvoiceComplexType;
    }
    
    @Override
    public ExtractedData extractData(Object xmlObject) {
        try {
            InvoiceComplexType invoice = (InvoiceComplexType) xmlObject;
            
            String nip = "DEFAULT_NIP"; // Default for Invoice type
            String p1 = invoice.getInvoiceNumber();
            String p2 = "INV-" + invoice.getInvoiceNumber();
            
            log.info("Extracted data from Invoice - NIP: {}, P1: {}, P2: {}", nip, p1, p2);
            
            return new ExtractedData(nip, p1, p2, "Invoice");
            
        } catch (Exception e) {
            throw new DataExtractionException("Failed to extract data from Invoice: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "InvoiceProcessor";
    }
    
    @Override
    public int getPriority() {
        return 5;
    }
}