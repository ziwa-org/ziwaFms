package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Monthly financial summary DTO
 * Requirements: 12.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFinancialSummary {
    private YearMonth month;
    private Double totalIncome;
    private Double totalExpenses;
    private Double netProfit;
}
