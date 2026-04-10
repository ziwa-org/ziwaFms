package com.example.ziwa.controller;

import com.example.ziwa.dto.*;
import com.example.ziwa.service.AnalyticsService;
import com.example.ziwa.service.MilkProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analytics controller providing dashboard and cross-module analytics
 * Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 4.3
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for dashboard data and cross-module analytics")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    /**
     * Get dashboard summary
     * Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        var dashboardData = analyticsService.getDashboardData();
        
        // Convert internal DTOs to response DTOs
        MonthlyFinancialSummary financialSummary = MonthlyFinancialSummary.builder()
            .month(YearMonth.now())
            .totalIncome(dashboardData.getMonthlyIncome())
            .totalExpenses(dashboardData.getMonthlyExpenses())
            .netProfit(dashboardData.getMonthlyNetProfit())
            .build();
        
        // Convert production trends
        List<DailyProduction> productionTrends = dashboardData.getProductionTrends().stream()
            .map(trend -> DailyProduction.builder()
                .date(trend.getDate())
                .totalProduction(trend.getTotalProduction())
                .averagePerCow(trend.getAveragePerCow())
                .recordCount(trend.getRecordCount())
                .build())
            .collect(Collectors.toList());
        
        // Convert top producers
        List<TopProducer> topProducers = dashboardData.getTopProducers().stream()
            .map(producer -> TopProducer.builder()
                .rank(producer.getRank())
                .cowId(producer.getCowId())
                .cowTagId(producer.getCowTagId())
                .totalProduction(producer.getTotalProduction())
                .recordCount(producer.getRecordCount())
                .build())
            .collect(Collectors.toList());
        
        // For now, upcoming vaccinations is empty (can be enhanced later)
        List<UpcomingVaccination> upcomingVaccinations = List.of();
        
        DashboardResponse response = DashboardResponse.builder()
            .activeCowsCount(dashboardData.getActiveCowsCount())
            .todayProduction(dashboardData.getTodayProduction())
            .monthlyFinancialSummary(financialSummary)
            .cowsInWithdrawal(dashboardData.getCowsInWithdrawal())
            .upcomingVaccinations(upcomingVaccinations)
            .productionTrend30Days(productionTrends)
            .topProducers(topProducers)
            .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Compare production periods
     * Requirements: 4.3
     */
    @GetMapping("/production-comparison")
    public ResponseEntity<ProductionComparisonResponse> compareProductionPeriods(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start1,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end1,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start2,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end2
    ) {
        var comparison = analyticsService.compareProductionPeriods(start1, end1, start2, end2);
        
        ProductionComparisonResponse response = ProductionComparisonResponse.builder()
            .period1Start(comparison.getPeriod1Start())
            .period1End(comparison.getPeriod1End())
            .period1Total(comparison.getPeriod1Total())
            .period1Average(comparison.getPeriod1Average())
            .period2Start(comparison.getPeriod2Start())
            .period2End(comparison.getPeriod2End())
            .period2Total(comparison.getPeriod2Total())
            .period2Average(comparison.getPeriod2Average())
            .changePercentage(comparison.getChangePercentage())
            .build();
        
        return ResponseEntity.ok(response);
    }
}
