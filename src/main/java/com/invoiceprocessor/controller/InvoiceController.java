package com.invoiceprocessor.controller;

import com.invoiceprocessor.dto.InvoiceRequest;
import com.invoiceprocessor.dto.InvoiceResponse;
import com.invoiceprocessor.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @PostMapping("/invoices")
    @Operation(summary = "Process XML invoice", description = "Processes a Base64-encoded XML invoice and saves it to the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invoice saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or XML validation failed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> processInvoice(@Valid @RequestBody InvoiceRequest request) throws Exception {
        log.info("Processing invoice request");
        invoiceService.processInvoice(request.getBase64xml());
        
        InvoiceResponse response = new InvoiceResponse("Invoice saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 