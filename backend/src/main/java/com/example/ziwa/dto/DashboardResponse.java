package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dashboard response DTO with all dashboard metrics
 * Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Integer activeCowsCount;
    private Double todayProduction;
    private MonthlyFinancialSummary monthlyFinancialSummary;
    private Integer cowsInWithdrawal;
    private List<UpcomingVaccination> upcomingVaccinations;
    private List<DailyProduction> productionTrend30Days;
    private List<TopProducer> topProducers;
}
