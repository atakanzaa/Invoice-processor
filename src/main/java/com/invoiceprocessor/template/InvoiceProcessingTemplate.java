package com.invoiceprocessor.template;

import com.invoiceprocessor.dto.ExtractedData;
import com.invoiceprocessor.dto.ProcessingResult;
import com.invoiceprocessor.exception.Base64DecodingException;
import com.invoiceprocessor.exception.XmlUnmarshallingException;
import com.invoiceprocessor.exception.XmlValidationException;
import lombok.extern.slf4j.Slf4j;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Template Method Pattern for invoice processing
 * Defines the skeleton of invoice processing algorithm
 */
@Slf4j
public abstract class InvoiceProcessingTemplate {
    
    /**
     * Template method defining the processing algorithm
     */
    public final ProcessingResult process(String base64xml) {
        try {
            onProcessingStarted();
            
            // Step 1: Decode Base64
            String xmlContent = decodeBase64(base64xml);
            onBase64Decoded(xmlContent);
            
            // Step 2: Validate XML
            validateXml(xmlContent);
            onXmlValidated();
            
            // Step 3: Parse XML
            Object xmlObject = parseXml(xmlContent);
            onXmlParsed(xmlObject);
            
            // Step 4: Extract data (strategy-specific)
            ExtractedData data = extractData(xmlObject);
            onDataExtracted(data);
            
            // Step 5: Save data
            String invoiceId = saveData(data);
            onDataSaved(invoiceId);
            
            onProcessingCompleted(invoiceId);
            return ProcessingResult.success(invoiceId, data);
            
        } catch (Exception e) {
            onProcessingFailed(e);
            throw e;
        }
    }
    
    // Template steps - some concrete, some abstract
    
    protected final String decodeBase64(String base64xml) {
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
    
    protected final void validateXml(String xmlContent) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = getSchema(factory);
            
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
        } catch (Exception e) {
            throw new XmlValidationException("XML validation failed: " + e.getMessage(), e);
        }
    }
    
    // Abstract methods - must be implemented by subclasses
    protected abstract Schema getSchema(SchemaFactory factory) throws Exception;
    protected abstract Object parseXml(String xmlContent);
    protected abstract ExtractedData extractData(Object xmlObject);
    protected abstract String saveData(ExtractedData data);
    
    // Hook methods - can be overridden by subclasses
    protected void onProcessingStarted() {
        log.info("🚀 Processing started");
    }
    
    protected void onBase64Decoded(String xmlContent) {
        log.debug("✅ Base64 decoded successfully, length: {}", xmlContent.length());
    }
    
    protected void onXmlValidated() {
        log.info("✅ XML validation completed");
    }
    
    protected void onXmlParsed(Object xmlObject) {
        log.info("✅ XML parsing completed: {}", xmlObject.getClass().getSimpleName());
    }
    
    protected void onDataExtracted(ExtractedData data) {
        log.info("📊 Data extracted: Type={}, NIP={}, P1={}, P2={}", 
            data.getProcessorType(), data.getNip(), data.getP1(), data.getP2());
    }
    
    protected void onDataSaved(String invoiceId) {
        log.info("💾 Data saved successfully with ID: {}", invoiceId);
    }
    
    protected void onProcessingCompleted(String invoiceId) {
        log.info("🎉 Processing completed successfully - Invoice ID: {}", invoiceId);
    }
    
    protected void onProcessingFailed(Exception e) {
        log.error("❌ Processing failed: {}", e.getMessage(), e);
    }
} 