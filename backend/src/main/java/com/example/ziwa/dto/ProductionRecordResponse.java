package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionRecordResponse {
    
    private Long id;
    private Long cowId;
    private String cowTagId;
    private LocalDate date;
    private Double morningQuantity;
    private Double eveningQuantity;
    private Double totalQuantity;
    private String notes;
    private LocalDateTime createdAt;
}
