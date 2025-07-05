package com.invoiceprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedData {
    private String nip;
    private String p1;
    private String p2;
    private String processorType;
}
