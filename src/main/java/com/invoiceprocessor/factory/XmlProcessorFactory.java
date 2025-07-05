package com.invoiceprocessor.factory;

import com.invoiceprocessor.strategy.XmlProcessorStrategy;
import com.invoiceprocessor.exception.DataExtractionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class XmlProcessorFactory {
    
    private final List<XmlProcessorStrategy> strategies;
    
    /**
     * Get the appropriate processor strategy for the given XML object
     */
    public XmlProcessorStrategy getProcessor(Object xmlObject) {
        log.debug("Finding processor for XML object type: {}", xmlObject.getClass().getSimpleName());
        
        return strategies.stream()
            .filter(strategy -> strategy.canHandle(xmlObject))
            .max(Comparator.comparingInt(XmlProcessorStrategy::getPriority))
            .orElseThrow(() -> new DataExtractionException(
                "No processor found for XML type: " + xmlObject.getClass().getSimpleName()));
    }
    
    /**
     * Get all available strategies
     */
    public List<XmlProcessorStrategy> getAllStrategies() {
        return strategies.stream()
            .sorted(Comparator.comparingInt(XmlProcessorStrategy::getPriority).reversed())
            .toList();
    }
}