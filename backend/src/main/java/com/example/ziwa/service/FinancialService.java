package com.example.ziwa.service;

import com.example.ziwa.dto.*;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.FinancialTransaction;
import com.example.ziwa.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialService {
    private final FinancialTransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        FinancialTransaction transaction = FinancialTransaction.builder()
            .date(request.getDate())
            .type(request.getType())
            .category(request.getCategory())
            .amount(request.getAmount())
            .description(request.getDescription())
            .referenceId(request.getReferenceId())
            .deleted(false)
            .build();
        
        FinancialTransaction saved = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(saved);
    }

    public TransactionResponse getTransactionById(Long id) {
        FinancialTransaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return TransactionResponse.fromEntity(transaction);
    }

    public Page<TransactionResponse> listTransactions(
        FinancialTransaction.TransactionType type,
        String category,
        LocalDate start,
        LocalDate end,
        Pageable pageable
    ) {
        // Default date range if not provided
        if (start == null) {
            start = LocalDate.now().minusYears(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }
        
        Page<FinancialTransaction> transactions;
        
        if (type != null && category != null) {
            transactions = transactionRepository.findByDeletedFalseAndTypeAndCategoryAndDateBetween(
                type, category, start, end, pageable
            );
        } else if (type != null) {
            transactions = transactionRepository.findByDeletedFalseAndTypeAndDateBetween(
                type, start, end, pageable
            );
        } else if (category != null) {
            transactions = transactionRepository.findByDeletedFalseAndCategoryAndDateBetween(
                category, start, end, pageable
            );
        } else {
            transactions = transactionRepository.findByDeletedFalseAndDateBetween(
                start, end, pageable
            );
        }
        
        return transactions.map(TransactionResponse::fromEntity);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        FinancialTransaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        transaction.setDate(request.getDate());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setReferenceId(request.getReferenceId());
        
        FinancialTransaction updated = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(updated);
    }

    @Transactional
    public void softDeleteTransaction(Long id) {
        FinancialTransaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }

    public ProfitLossResponse calculateProfitLoss(LocalDate start, LocalDate end) {
        Double totalIncome = transactionRepository.sumByTypeAndDateBetween(
            FinancialTransaction.TransactionType.INCOME, start, end
        );
        Double totalExpenses = transactionRepository.sumByTypeAndDateBetween(
            FinancialTransaction.TransactionType.EXPENSE, start, end
        );
        
        // Handle null values
        totalIncome = totalIncome != null ? totalIncome : 0.0;
        totalExpenses = totalExpenses != null ? totalExpenses : 0.0;
        
        double netProfit = totalIncome - totalExpenses;
        double profitMargin = totalIncome > 0 ? (netProfit / totalIncome) * 100 : 0.0;
        
        return ProfitLossResponse.builder()
            .startDate(start)
            .endDate(end)
            .totalIncome(totalIncome)
            .totalExpenses(totalExpenses)
            .netProfit(netProfit)
            .profitMargin(profitMargin)
            .build();
    }

    public List<CategoryBreakdownResponse> getIncomeBreakdown(LocalDate start, LocalDate end) {
        return getCategoryBreakdown(FinancialTransaction.TransactionType.INCOME, start, end);
    }

    public List<CategoryBreakdownResponse> getExpenseBreakdown(LocalDate start, LocalDate end) {
        return getCategoryBreakdown(FinancialTransaction.TransactionType.EXPENSE, start, end);
    }

    private List<CategoryBreakdownResponse> getCategoryBreakdown(
        FinancialTransaction.TransactionType type,
        LocalDate start,
        LocalDate end
    ) {
        List<Object[]> results = transactionRepository.getBreakdownByCategory(type, start, end);
        
        // Calculate total for percentage calculation
        double grandTotal = results.stream()
            .mapToDouble(row -> ((Number) row[1]).doubleValue())
            .sum();
        
        List<CategoryBreakdownResponse> breakdown = new ArrayList<>();
        for (Object[] row : results) {
            String category = (String) row[0];
            Double total = ((Number) row[1]).doubleValue();
            Integer count = ((Number) row[2]).intValue();
            Double percentage = grandTotal > 0 ? (total / grandTotal) * 100 : 0.0;
            
            breakdown.add(CategoryBreakdownResponse.builder()
                .category(category)
                .total(total)
                .percentage(percentage)
                .transactionCount(count)
                .build());
        }
        
        return breakdown;
    }

    public List<MonthlyTrendDTO> getFinancialTrends(LocalDate start, LocalDate end) {
        List<Object[]> results = transactionRepository.getMonthlyTrends(start, end);
        
        List<MonthlyTrendDTO> trends = new ArrayList<>();
        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            Double income = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Double expenses = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
            
            trends.add(MonthlyTrendDTO.builder()
                .month(YearMonth.of(year, month))
                .totalIncome(income)
                .totalExpenses(expenses)
                .netProfit(income - expenses)
                .build());
        }
        
        return trends;
    }
}
