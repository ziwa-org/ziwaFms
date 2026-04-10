package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Daily production DTO for dashboard trends
 * Requirements: 12.6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyProduction {
    private LocalDate date;
    private Double totalProduction;
    private Double averagePerCow;
    private Integer recordCount;
}
