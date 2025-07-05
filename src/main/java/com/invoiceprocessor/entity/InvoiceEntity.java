package com.invoiceprocessor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;




@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nip", nullable = false)
    private String nip;
    
    @Column(name = "p1", nullable = false)
    private String p1;
    
    @Column(name = "p2", nullable = false)
    private String p2;
    
    
    
    
} 