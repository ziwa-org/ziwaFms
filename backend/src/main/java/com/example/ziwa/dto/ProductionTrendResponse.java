package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionTrendResponse {
    
    private LocalDate date;
    private Double totalProduction;
    private Double averagePerCow;
    private Integer recordCount;
}
