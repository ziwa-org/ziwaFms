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
public class ProfitLossResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalIncome;
    private Double totalExpenses;
    private Double netProfit;
    private Double profitMargin;
}
