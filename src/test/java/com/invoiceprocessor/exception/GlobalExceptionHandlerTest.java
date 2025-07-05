package com.invoiceprocessor.exception;

import com.invoiceprocessor.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/invoices");
    }

    @Test
    @DisplayName("Should handle Base64DecodingException correctly")
    void shouldHandleBase64DecodingException() {
        // Given
        Base64DecodingException exception = new Base64DecodingException("Invalid Base64 format");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleBase64DecodingException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("BASE64_DECODING_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid Base64 format");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle XmlValidationException correctly")
    void shouldHandleXmlValidationException() {
        // Given
        XmlValidationException exception = new XmlValidationException("XML does not conform to XSD");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleXmlValidationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("XML_VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("XML does not conform to XSD");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle XmlUnmarshallingException correctly")
    void shouldHandleXmlUnmarshallingException() {
        // Given
        XmlUnmarshallingException exception = new XmlUnmarshallingException("Failed to unmarshal XML");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleXmlUnmarshallingException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("XML_UNMARSHALLING_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to unmarshal XML");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle DataExtractionException correctly")
    void shouldHandleDataExtractionException() {
        // Given
        DataExtractionException exception = new DataExtractionException("Failed to extract NIP field");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleDataExtractionException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("DATA_EXTRACTION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to extract NIP field");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException correctly")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("invoiceRequest", "base64xml", "must not be blank");
        FieldError fieldError2 = new FieldError("invoiceRequest", "someField", "must not be null");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleValidationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).contains("base64xml: must not be blank");
        assertThat(response.getBody().getMessage()).contains("someField: must not be null");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException correctly")
    void shouldHandleConstraintViolationException() {
        // Given
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        
        // Mock Path objects for property paths
        jakarta.validation.Path path1 = mock(jakarta.validation.Path.class);
        jakarta.validation.Path path2 = mock(jakarta.validation.Path.class);
        
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(path1.toString()).thenReturn("field1");
        when(violation1.getMessage()).thenReturn("must be valid");
        
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(path2.toString()).thenReturn("field2");
        when(violation2.getMessage()).thenReturn("must not be empty");
        
        when(exception.getConstraintViolations()).thenReturn(Set.of(violation1, violation2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleConstraintViolationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("CONSTRAINT_VIOLATION");
        assertThat(response.getBody().getMessage()).contains("field1: must be valid");
        assertThat(response.getBody().getMessage()).contains("field2: must not be empty");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException correctly")
    void shouldHandleHttpMessageNotReadableException() {
        // Given
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("JSON parse error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleHttpMessageNotReadableException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("MALFORMED_JSON");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid JSON format in request body");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException correctly")
    void shouldHandleMethodArgumentTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("invalidParam");
        when(exception.getMessage()).thenReturn("Type mismatch error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleMethodArgumentTypeMismatchException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("ARGUMENT_TYPE_MISMATCH");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument type: invalidParam");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle DataAccessException correctly")
    void shouldHandleDataAccessException() {
        // Given
        DataAccessException exception = mock(DataAccessException.class);
        when(exception.getMessage()).thenReturn("Database connection failed");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleDataAccessException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("DATABASE_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Database operation failed");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle generic RuntimeException correctly")
    void shouldHandleRuntimeException() {
        // Given
        RuntimeException exception = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleRuntimeException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("RUNTIME_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Something went wrong");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleGenericException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getPath()).isEqualTo("/api/invoices");
    }

    @Test
    @DisplayName("Should preserve exception message in custom exceptions")
    void shouldPreserveExceptionMessageInCustomExceptions() {
        // Given
        String customMessage = "Custom error message with details";
        Base64DecodingException exception = new Base64DecodingException(customMessage);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleBase64DecodingException(exception, request);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void shouldHandleExceptionWithNullMessage() {
        // Given
        RuntimeException exception = new RuntimeException((String) null);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
            .handleRuntimeException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("RUNTIME_ERROR");
        // Should handle null message gracefully
    }

    @Test
    @DisplayName("Should set correct HTTP status codes for different exception types")
    void shouldSetCorrectHttpStatusCodes() {
        // Test BAD_REQUEST exceptions
        assertThat(globalExceptionHandler
            .handleBase64DecodingException(new Base64DecodingException("test"), request)
            .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        assertThat(globalExceptionHandler
            .handleXmlValidationException(new XmlValidationException("test"), request)
            .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        assertThat(globalExceptionHandler
            .handleRuntimeException(new RuntimeException("test"), request)
            .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test INTERNAL_SERVER_ERROR exceptions
        assertThat(globalExceptionHandler
            .handleDataAccessException(mock(DataAccessException.class), request)
            .getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        assertThat(globalExceptionHandler
            .handleGenericException(new Exception("test"), request)
            .getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 