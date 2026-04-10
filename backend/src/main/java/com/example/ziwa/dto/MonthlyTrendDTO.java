package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendDTO {
    private YearMonth month;
    private Double totalIncome;
    private Double totalExpenses;
    private Double netProfit;
}
