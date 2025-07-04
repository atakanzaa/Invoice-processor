package com.invoiceprocessor.service;

import com.example.generated.FakturaComplexType;
import com.example.generated.InvoiceComplexType;
import com.example.generated.ObjectFactory;
import com.invoiceprocessor.entity.InvoiceEntity;
import com.invoiceprocessor.repository.InvoiceRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    
    public void processInvoice(String base64xml) throws Exception {
        // 1. Decode Base64 to XML
        String xmlContent = decodeBase64ToXml(base64xml);
        log.info("Decoded XML content: {}", xmlContent);
        
        // 2. Validate XML against XSD
        validateXmlAgainstXsd(xmlContent);
        log.info("XML validation successful");
        
        // 3. Unmarshal XML to Java objects
        Object xmlObject = unmarshalXml(xmlContent);
        log.info("XML unmarshalling successful");
        
        // 4. Extract required fields
        String nip = extractNip(xmlObject);
        String p1 = extractP1(xmlObject);
        String p2 = extractP2(xmlObject);
        
        log.info("Extracted data - NIP: {}, P1: {}, P2: {}", nip, p1, p2);
        
        // 5. Save to database
        InvoiceEntity entity = new InvoiceEntity();
        entity.setNip(nip);
        entity.setP1(p1);
        entity.setP2(p2);
        
        invoiceRepository.save(entity);
        log.info("Invoice saved successfully");
    }
    
    private String decodeBase64ToXml(String base64xml) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64xml);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 encoding: " + e.getMessage(), e);
        }
    }
    
    private void validateXmlAgainstXsd(String xmlContent) throws Exception {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ClassPathResource xsdResource = new ClassPathResource("xsd/schemat.xsd");
            Schema schema = factory.newSchema(xsdResource.getURL());
            
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
        } catch (Exception e) {
            throw new RuntimeException("XML validation failed: " + e.getMessage(), e);
        }
    }
    
    private Object unmarshalXml(String xmlContent) throws JAXBException {
        try {
            // Create a new JAXB context with the updated generated classes
            JAXBContext context = JAXBContext.newInstance("com.example.generated");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object result = unmarshaller.unmarshal(new StringReader(xmlContent));
            
            if (result instanceof JAXBElement) {
                return ((JAXBElement<?>) result).getValue();
            }
            return result;
        } catch (JAXBException e) {
            throw new RuntimeException("XML unmarshalling failed: " + e.getMessage(), e);
        }
    }
    
    private String extractNip(Object xmlObject) {
        try {
            if (xmlObject instanceof FakturaComplexType) {
                FakturaComplexType faktura = (FakturaComplexType) xmlObject;
                return faktura.getPodmiot1().getDaneIdentyfikacyjne().getNIP();
            } else if (xmlObject instanceof InvoiceComplexType) {
                // For Invoice type, use default values
                return "DEFAULT_NIP";
            } else {
                throw new RuntimeException("Unknown XML type: " + xmlObject.getClass().getSimpleName());
            }
        } catch (Exception e) {
            throw new RuntimeException("NIP field not found in XML structure", e);
        }
    }
    
    private String extractP1(Object xmlObject) {
        try {
            if (xmlObject instanceof FakturaComplexType) {
                FakturaComplexType faktura = (FakturaComplexType) xmlObject;
                return faktura.getFa().getP1() != null ? faktura.getFa().getP1().toString() : "";
            } else if (xmlObject instanceof InvoiceComplexType) {
                InvoiceComplexType invoice = (InvoiceComplexType) xmlObject;
                return invoice.getInvoiceNumber();
            } else {
                return "DEFAULT_P1";
            }
        } catch (Exception e) {
            throw new RuntimeException("P_1 field not found in XML structure", e);
        }
    }
    
    private String extractP2(Object xmlObject) {
        try {
            if (xmlObject instanceof FakturaComplexType) {
                FakturaComplexType faktura = (FakturaComplexType) xmlObject;
                return faktura.getFa().getP2();
            } else if (xmlObject instanceof InvoiceComplexType) {
                InvoiceComplexType invoice = (InvoiceComplexType) xmlObject;
                return "INV-" + invoice.getInvoiceNumber();
            } else {
                return "DEFAULT_P2";
            }
        } catch (Exception e) {
            throw new RuntimeException("P_2 field not found in XML structure", e);
        }
    }
} 