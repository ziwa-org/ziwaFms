package com.example.ziwa.service;

import com.example.ziwa.model.Cow.CowStatus;
import com.example.ziwa.repository.CowRepository;
import com.example.ziwa.repository.MilkProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Analytics service providing cross-module reporting and dashboard data
 * Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 4.3
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {
    private final CowRepository cowRepository;
    private final MilkProductionRepository milkProductionRepository;
    private final FinancialService financialService;
    private final HealthService healthService;
    private final MilkProductionService milkProductionService;

    /**
     * Get dashboard data aggregating from all modules
     * Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6
     */
    public DashboardData getDashboardData() {
        // Requirement 12.1: Active cows count
        Long activeCowsCount = cowRepository.countByStatus(CowStatus.ACTIVE);
        
        // Requirement 12.2: Today's total milk production
        LocalDate today = LocalDate.now();
        Double todayProduction = milkProductionRepository.getTotalProductionForDate(today);
        if (todayProduction == null) {
            todayProduction = 0.0;
        }
        
        // Requirement 12.3: Current month's financial summary
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        var profitLoss = financialService.calculateProfitLoss(monthStart, monthEnd);
        
        // Requirement 12.4: Cows currently in withdrawal period
        var activeWithdrawals = healthService.getActiveWithdrawals();
        Integer cowsInWithdrawal = activeWithdrawals.size();
        
        // Requirement 12.6: Production trends for the last 30 days
        LocalDate thirtyDaysAgo = today.minusDays(30);
        var productionTrends = milkProductionService.getProductionTrends(thirtyDaysAgo, today);
        
        // Top producers (for dashboard)
        var topProducers = milkProductionService.getTopProducers(5);
        
        return DashboardData.builder()
            .activeCowsCount(activeCowsCount.intValue())
            .todayProduction(todayProduction)
            .monthlyIncome(profitLoss.getTotalIncome())
            .monthlyExpenses(profitLoss.getTotalExpenses())
            .monthlyNetProfit(profitLoss.getNetProfit())
            .cowsInWithdrawal(cowsInWithdrawal)
            .productionTrends(productionTrends)
            .topProducers(topProducers)
            .activeWithdrawals(activeWithdrawals)
            .build();
    }

    /**
     * Compare production across two time periods
     * Requirements: 4.3
     */
    public ProductionComparison compareProductionPeriods(
        LocalDate start1, LocalDate end1,
        LocalDate start2, LocalDate end2
    ) {
        // Get trends for both periods
        var period1Trends = milkProductionService.getProductionTrends(start1, end1);
        var period2Trends = milkProductionService.getProductionTrends(start2, end2);
        
        // Calculate totals for each period
        double period1Total = period1Trends.stream()
            .mapToDouble(MilkProductionService.ProductionTrendDTO::getTotalProduction)
            .sum();
        
        double period2Total = period2Trends.stream()
            .mapToDouble(MilkProductionService.ProductionTrendDTO::getTotalProduction)
            .sum();
        
        // Calculate averages
        double period1Average = period1Trends.isEmpty() ? 0.0 : 
            period1Total / period1Trends.size();
        double period2Average = period2Trends.isEmpty() ? 0.0 : 
            period2Total / period2Trends.size();
        
        // Calculate change percentage
        double changePercentage = period1Total > 0 ? 
            ((period2Total - period1Total) / period1Total) * 100 : 0.0;
        
        return ProductionComparison.builder()
            .period1Start(start1)
            .period1End(end1)
            .period1Total(period1Total)
            .period1Average(period1Average)
            .period2Start(start2)
            .period2End(end2)
            .period2Total(period2Total)
            .period2Average(period2Average)
            .changePercentage(changePercentage)
            .build();
    }

    // DTO classes for analytics responses
    
    @lombok.Data
    @lombok.Builder
    public static class DashboardData {
        private Integer activeCowsCount;
        private Double todayProduction;
        private Double monthlyIncome;
        private Double monthlyExpenses;
        private Double monthlyNetProfit;
        private Integer cowsInWithdrawal;
        private List<MilkProductionService.ProductionTrendDTO> productionTrends;
        private List<MilkProductionService.TopProducerDTO> topProducers;
        private List<com.example.ziwa.dto.WithdrawalResponse> activeWithdrawals;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ProductionComparison {
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
}
