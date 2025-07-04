package com.invoiceprocessor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InvoiceRequest {
    
    @NotBlank(message = "base64xml field is required")
    private String base64xml;
} 