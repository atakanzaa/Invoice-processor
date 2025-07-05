package com.invoiceprocessor.service;

import com.invoiceprocessor.chain.ValidationChainFactory;
import com.invoiceprocessor.chain.ValidationHandler;
import com.invoiceprocessor.dto.ExtractedData;
import com.invoiceprocessor.dto.ProcessingResult;
import com.invoiceprocessor.dto.ValidationContext;
import com.invoiceprocessor.entity.InvoiceEntity;
import com.invoiceprocessor.exception.Base64DecodingException;
import com.invoiceprocessor.exception.XmlUnmarshallingException;
import com.invoiceprocessor.exception.XmlValidationException;
import com.invoiceprocessor.factory.XmlProcessorFactory;
import com.invoiceprocessor.observer.InvoiceProcessingObserver;
import com.invoiceprocessor.repository.InvoiceRepository;
import com.invoiceprocessor.strategy.XmlProcessorStrategy;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final XmlProcessorFactory processorFactory;
    private final List<InvoiceProcessingObserver> observers;
    private final ValidationChainFactory validationChainFactory;
    
    public ProcessingResult processInvoice(String base64xml) {
        try {
            notifyObservers(obs -> obs.onProcessingStarted("Unknown"));
            
            // 1. Decode Base64 to XML
            String xmlContent = decodeBase64ToXml(base64xml);
            log.info("Decoded XML content: {}", xmlContent);
            
            // 2. Validate XML using Chain of Responsibility
            ValidationContext validationContext = new ValidationContext(xmlContent);
            ValidationHandler validationChain = validationChainFactory.createValidationChain();
            validationChain.handle(validationContext);
            notifyObservers(obs -> obs.onValidationCompleted("XML"));
            
            // 3. Unmarshal XML to Java objects
            Object xmlObject = unmarshalXml(xmlContent);
            log.info("XML unmarshalling successful");
            
            // 4. Use Strategy Pattern to extract data
            XmlProcessorStrategy processor = processorFactory.getProcessor(xmlObject);
            ExtractedData extractedData = processor.extractData(xmlObject);
            notifyObservers(obs -> obs.onDataExtracted(extractedData));
            
            // 5. Save to database
            String invoiceId = saveToDatabase(extractedData);
            notifyObservers(obs -> obs.onProcessingCompleted(invoiceId));
            
            return ProcessingResult.success(invoiceId, extractedData);
            
        } catch (Exception e) {
            notifyObservers(obs -> obs.onProcessingFailed(e.getMessage()));
            throw e;
        }
    }
    
    private String saveToDatabase(ExtractedData data) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setNip(data.getNip());
        entity.setP1(data.getP1());
        entity.setP2(data.getP2());
        
        InvoiceEntity saved = invoiceRepository.save(entity);
        log.info("Invoice saved successfully with ID: {}", saved.getId());
        
        return saved.getId().toString();
    }
    
    private void notifyObservers(Consumer<InvoiceProcessingObserver> action) {
        observers.forEach(action);
    }
    
    private String decodeBase64ToXml(String base64xml) {
        try {
            if (base64xml == null) {
                throw new Base64DecodingException("Base64 string cannot be null");
            }
            if (base64xml.trim().isEmpty()) {
                throw new Base64DecodingException("Base64 string cannot be empty");
            }
            byte[] decodedBytes = Base64.getDecoder().decode(base64xml);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Base64DecodingException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new Base64DecodingException("Invalid Base64 encoding: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Base64DecodingException("Failed to decode Base64: " + e.getMessage(), e);
        }
    }
    
    private Object unmarshalXml(String xmlContent) {
        try {
            JAXBContext context = JAXBContext.newInstance("com.example.generated");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object result = unmarshaller.unmarshal(new StringReader(xmlContent));
            
            if (result instanceof JAXBElement) {
                return ((JAXBElement<?>) result).getValue();
            }
            return result;
        } catch (JAXBException e) {
            throw new XmlUnmarshallingException("XML unmarshalling failed: " + e.getMessage(), e);
        }
    }
}