package com.invoiceprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingResult {
    private boolean success;
    private String message;
    private String invoiceId;
    private ExtractedData extractedData;
    private String errorDetails;
    
    public static ProcessingResult success(String invoiceId, ExtractedData data) {
        return new ProcessingResult(true, "Invoice processed successfully", invoiceId, data, null);
    }
    
    public static ProcessingResult failure(String message, String errorDetails) {
        return new ProcessingResult(false, message, null, null, errorDetails);
    }
}