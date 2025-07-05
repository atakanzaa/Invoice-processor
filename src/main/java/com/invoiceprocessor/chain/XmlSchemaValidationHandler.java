package com.invoiceprocessor.chain;

import com.invoiceprocessor.dto.ValidationContext;
import com.invoiceprocessor.exception.XmlValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;

/**
 * Validates XML against XSD schema
 */
@Component
public class XmlSchemaValidationHandler extends ValidationHandler {
    
    @Override
    protected boolean canHandle(ValidationContext context) {
        return context.getXmlContent() != null;
    }
    
    @Override
    protected void doValidation(ValidationContext context) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ClassPathResource xsdResource = new ClassPathResource("xsd/schemat.xsd");
            Schema schema = factory.newSchema(xsdResource.getURL());
            
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(context.getXmlContent())));
            
            // Store schema validation success in context
            context.setAttribute("schemaValidated", true);
            
        } catch (Exception e) {
            throw new XmlValidationException("XSD schema validation failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected String getHandlerName() {
        return "XSDSchema";
    }
} 