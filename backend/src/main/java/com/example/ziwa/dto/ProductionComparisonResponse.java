package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Production comparison response DTO
 * Requirements: 4.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionComparisonResponse {
    private LocalDate period1Start;
    private LocalDate period1End;
    private Double period1Total;
    private Double period1Average;
    private LocalDate period2Start;
    private LocalDate period2End;
    private Double period2Total;
    private Double period2Average;
    private Double changePercentage;
}
