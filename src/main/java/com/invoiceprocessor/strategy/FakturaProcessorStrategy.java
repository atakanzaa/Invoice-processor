package com.invoiceprocessor.strategy;

import com.example.generated.FakturaComplexType;
import com.invoiceprocessor.dto.ExtractedData;
import com.invoiceprocessor.exception.DataExtractionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FakturaProcessorStrategy implements XmlProcessorStrategy {
    
    @Override
    public boolean canHandle(Object xmlObject) {
        return xmlObject instanceof FakturaComplexType;
    }
    
    @Override
    public ExtractedData extractData(Object xmlObject) {
        try {
            FakturaComplexType faktura = (FakturaComplexType) xmlObject;
            
            String nip = faktura.getPodmiot1().getDaneIdentyfikacyjne().getNIP();
            String p1 = faktura.getFa().getP1() != null ? faktura.getFa().getP1().toString() : "";
            String p2 = faktura.getFa().getP2();
            
            log.info("Extracted data from Faktura - NIP: {}, P1: {}, P2: {}", nip, p1, p2);
            
            return new ExtractedData(nip, p1, p2, "Faktura");
            
        } catch (Exception e) {
            throw new DataExtractionException("Failed to extract data from Faktura: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "FakturaProcessor";
    }
    
    @Override
    public int getPriority() {
        return 10;
    }
}