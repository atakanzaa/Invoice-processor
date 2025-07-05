package com.invoiceprocessor.service;

import com.invoiceprocessor.chain.ValidationChainFactory;
import com.invoiceprocessor.chain.ValidationHandler;
import com.invoiceprocessor.dto.ExtractedData;
import com.invoiceprocessor.dto.ProcessingResult;
import com.invoiceprocessor.entity.InvoiceEntity;
import com.invoiceprocessor.exception.Base64DecodingException;
import com.invoiceprocessor.exception.DataExtractionException;
import com.invoiceprocessor.exception.XmlValidationException;
import com.invoiceprocessor.factory.XmlProcessorFactory;
import com.invoiceprocessor.observer.InvoiceProcessingObserver;
import com.invoiceprocessor.repository.InvoiceRepository;
import com.invoiceprocessor.strategy.XmlProcessorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceService Tests")
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    
    @Mock
    private XmlProcessorFactory processorFactory;
    
    @Mock
    private ValidationChainFactory validationChainFactory;
    
    @Mock
    private List<InvoiceProcessingObserver> observers;
    
    @Mock
    private XmlProcessorStrategy xmlProcessorStrategy;
    
    @Mock
    private ValidationHandler validationHandler;
    
    @Mock
    private InvoiceProcessingObserver observer;

    @InjectMocks
    private InvoiceService invoiceService;

    private String validFakturaXml;
    private String validFakturaBase64;
    private String validInvoiceXml;
    private String validInvoiceBase64;

    @BeforeEach
    void setUp() {
        validFakturaXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Faktura xmlns="http://crd.gov.pl/wzor/2023/06/29/12648/">
                <Naglowek>
                    <KodFormularza>FA</KodFormularza>
                    <WariantFormularza>1</WariantFormularza>
                    <DataWytworzeniaFa>2023-12-01T10:30:00</DataWytworzeniaFa>
                    <SystemInfo>Test System v1.0</SystemInfo>
                </Naglowek>
                <Podmiot1>
                    <DaneIdentyfikacyjne>
                        <NIP>1234567890</NIP>
                        <Nazwa>Test Company</Nazwa>
                    </DaneIdentyfikacyjne>
                    <Adres>
                        <KodKraju>PL</KodKraju>
                        <AdresL1>Test Address</AdresL1>
                    </Adres>
                </Podmiot1>
                <Podmiot2>
                    <DaneIdentyfikacyjne>
                        <NIP>9876543210</NIP>
                        <Nazwa>Client Company</Nazwa>
                    </DaneIdentyfikacyjne>
                </Podmiot2>
                <Fa>
                    <KodWaluty>PLN</KodWaluty>
                    <P_1>2023-01-15</P_1>
                    <P_2>INV-001</P_2>
                    <P_15>1000.00</P_15>
                </Fa>
            </Faktura>
            """;
        
        validInvoiceXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Invoice xmlns="http://crd.gov.pl/wzor/2023/06/29/12648/">
                <InvoiceNumber>SIMPLE-INV-123</InvoiceNumber>
            </Invoice>
            """;

        validFakturaBase64 = Base64.getEncoder().encodeToString(validFakturaXml.getBytes());
        validInvoiceBase64 = Base64.getEncoder().encodeToString(validInvoiceXml.getBytes());
        
        // Setup common mocks - use lenient to avoid unnecessary stubbing errors
        lenient().when(validationChainFactory.createValidationChain()).thenReturn(validationHandler);
        lenient().doNothing().when(validationHandler).handle(any());
        lenient().doNothing().when(observers).forEach(any());
    }

    @Test
    @DisplayName("Should process valid Faktura XML successfully")
    void shouldProcessValidFakturaXml() {
        // Given
        ExtractedData extractedData = new ExtractedData("1234567890", "2023-01-15", "INV-001", "Faktura");
        InvoiceEntity savedEntity = new InvoiceEntity();
        savedEntity.setId(1L);
        savedEntity.setNip("1234567890");
        savedEntity.setP1("2023-01-15");
        savedEntity.setP2("INV-001");

        when(processorFactory.getProcessor(any())).thenReturn(xmlProcessorStrategy);
        when(xmlProcessorStrategy.extractData(any())).thenReturn(extractedData);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(savedEntity);

        // When
        ProcessingResult result = invoiceService.processInvoice(validFakturaBase64);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getInvoiceId()).isEqualTo("1");
        assertThat(result.getExtractedData()).isEqualTo(extractedData);

        verify(validationChainFactory).createValidationChain();
        verify(validationHandler).handle(any());
        verify(processorFactory).getProcessor(any());
        verify(xmlProcessorStrategy).extractData(any());
        verify(invoiceRepository).save(any(InvoiceEntity.class));
    }

    @Test
    @DisplayName("Should process valid Invoice XML successfully")
    void shouldProcessValidInvoiceXml() {
        // Given
        ExtractedData extractedData = new ExtractedData("DEFAULT_NIP", "SIMPLE-INV-123", "INV-SIMPLE-INV-123", "Invoice");
        InvoiceEntity savedEntity = new InvoiceEntity();
        savedEntity.setId(2L);

        when(processorFactory.getProcessor(any())).thenReturn(xmlProcessorStrategy);
        when(xmlProcessorStrategy.extractData(any())).thenReturn(extractedData);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(savedEntity);

        // When
        ProcessingResult result = invoiceService.processInvoice(validInvoiceBase64);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getInvoiceId()).isEqualTo("2");
        verify(invoiceRepository).save(any(InvoiceEntity.class));
    }

    @Test
    @DisplayName("Should throw Base64DecodingException for invalid Base64")
    void shouldThrowBase64DecodingExceptionForInvalidBase64() {
        // Given
        String invalidBase64 = "invalid-base64-string";

        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(invalidBase64))
            .isInstanceOf(Base64DecodingException.class)
            .hasMessageContaining("Invalid Base64 encoding");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw XmlValidationException for invalid XML")
    void shouldThrowXmlValidationExceptionForInvalidXml() {
        // Given
        String invalidXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <InvalidRoot>
                <SomeElement>Invalid content</SomeElement>
            </InvalidRoot>
            """;
        String invalidXmlBase64 = Base64.getEncoder().encodeToString(invalidXml.getBytes());
        
        doThrow(new XmlValidationException("XML validation failed")).when(validationHandler).handle(any());

        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(invalidXmlBase64))
            .isInstanceOf(XmlValidationException.class)
            .hasMessageContaining("XML validation failed");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw XmlValidationException for missing required fields")
    void shouldThrowXmlValidationExceptionForMissingRequiredFields() {
        // Given
        String incompleteXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Faktura xmlns="http://crd.gov.pl/wzor/2023/06/29/12648/">
                <Naglowek>
                    <KodFormularza>FA</KodFormularza>
                </Naglowek>
            </Faktura>
            """;
        String incompleteXmlBase64 = Base64.getEncoder().encodeToString(incompleteXml.getBytes());
        
        doThrow(new XmlValidationException("Missing required fields")).when(validationHandler).handle(any());

        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(incompleteXmlBase64))
            .isInstanceOf(XmlValidationException.class)
            .hasMessageContaining("Missing required fields");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DataExtractionException when processor not found")
    void shouldThrowDataExtractionExceptionWhenProcessorNotFound() {
        // Given
        when(processorFactory.getProcessor(any())).thenThrow(new DataExtractionException("No processor found"));

        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(validFakturaBase64))
            .isInstanceOf(DataExtractionException.class)
            .hasMessageContaining("No processor found");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle empty Base64 string")
    void shouldHandleEmptyBase64String() {
        // Given
        String emptyBase64 = "";

        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(emptyBase64))
            .isInstanceOf(Base64DecodingException.class)
            .hasMessageContaining("Base64 string cannot be empty");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null Base64 string")
    void shouldHandleNullBase64String() {
        // When & Then
        assertThatThrownBy(() -> invoiceService.processInvoice(null))
            .isInstanceOf(Base64DecodingException.class)
            .hasMessageContaining("Base64 string cannot be null");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {
        // Given
        ExtractedData extractedData = new ExtractedData("TEST_NIP", "TEST_P1", "TEST_P2", "Test");
        InvoiceEntity savedEntity = new InvoiceEntity();
        savedEntity.setId(42L);

        when(processorFactory.getProcessor(any())).thenReturn(xmlProcessorStrategy);
        when(xmlProcessorStrategy.extractData(any())).thenReturn(extractedData);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(savedEntity);

        // When
        ProcessingResult result = invoiceService.processInvoice(validFakturaBase64);

        // Then
        ArgumentCaptor<InvoiceEntity> entityCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
        verify(invoiceRepository).save(entityCaptor.capture());

        InvoiceEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getNip()).isEqualTo("TEST_NIP");
        assertThat(capturedEntity.getP1()).isEqualTo("TEST_P1");
        assertThat(capturedEntity.getP2()).isEqualTo("TEST_P2");
        assertThat(result.getInvoiceId()).isEqualTo("42");
    }
} 