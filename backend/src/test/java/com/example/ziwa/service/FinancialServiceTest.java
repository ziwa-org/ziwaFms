package com.example.ziwa.service;

import com.example.ziwa.dto.*;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.FinancialTransaction;
import com.example.ziwa.repository.FinancialTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialServiceTest {

    @Mock
    private FinancialTransactionRepository transactionRepository;

    @InjectMocks
    private FinancialService financialService;

    private FinancialTransaction testTransaction;
    private TransactionRequest testRequest;

    @BeforeEach
    void setUp() {
        testTransaction = FinancialTransaction.builder()
                .id(1L)
                .date(LocalDate.now())
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("MILK_SALES")
                .amount(1000.0)
                .description("Milk sales for the week")
                .referenceId("REF001")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = TransactionRequest.builder()
                .date(LocalDate.now())
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("MILK_SALES")
                .amount(1000.0)
                .description("Milk sales for the week")
                .referenceId("REF001")
                .build();
    }

    @Test
    void createTransaction_WithValidData_ShouldSucceed() {
        // Arrange
        when(transactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);

        // Act
        TransactionResponse result = financialService.createTransaction(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1000.0, result.getAmount());
        assertEquals("MILK_SALES", result.getCategory());
        assertEquals(FinancialTransaction.TransactionType.INCOME, result.getType());
        verify(transactionRepository).save(any(FinancialTransaction.class));
    }

    @Test
    void getTransactionById_WhenExists_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        TransactionResponse result = financialService.getTransactionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1000.0, result.getAmount());
    }

    @Test
    void getTransactionById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> financialService.getTransactionById(999L));
    }

    @Test
    void listTransactions_WithTypeFilter_ShouldFilterCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        Page<FinancialTransaction> expectedPage = new PageImpl<>(List.of(testTransaction));
        
        when(transactionRepository.findByDeletedFalseAndTypeAndDateBetween(
                FinancialTransaction.TransactionType.INCOME, start, end, pageable))
                .thenReturn(expectedPage);

        // Act
        Page<TransactionResponse> result = financialService.listTransactions(
                FinancialTransaction.TransactionType.INCOME, null, start, end, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByDeletedFalseAndTypeAndDateBetween(
                FinancialTransaction.TransactionType.INCOME, start, end, pageable);
    }

    @Test
    void listTransactions_WithCategoryFilter_ShouldFilterCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        Page<FinancialTransaction> expectedPage = new PageImpl<>(List.of(testTransaction));
        
        when(transactionRepository.findByDeletedFalseAndCategoryAndDateBetween(
                "MILK_SALES", start, end, pageable))
                .thenReturn(expectedPage);

        // Act
        Page<TransactionResponse> result = financialService.listTransactions(
                null, "MILK_SALES", start, end, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateTransaction_WithValidData_ShouldSucceed() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);

        TransactionRequest updateRequest = TransactionRequest.builder()
                .date(LocalDate.now())
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("LIVESTOCK_SALES")
                .amount(2000.0)
                .description("Updated description")
                .build();

        // Act
        TransactionResponse result = financialService.updateTransaction(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void softDeleteTransaction_ShouldMarkAsDeleted() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);

        // Act
        financialService.softDeleteTransaction(1L);

        // Assert
        assertTrue(testTransaction.getDeleted());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void calculateProfitLoss_ShouldCalculateCorrectly() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        
        when(transactionRepository.sumByTypeAndDateBetween(
                FinancialTransaction.TransactionType.INCOME, start, end))
                .thenReturn(5000.0);
        when(transactionRepository.sumByTypeAndDateBetween(
                FinancialTransaction.TransactionType.EXPENSE, start, end))
                .thenReturn(3000.0);

        // Act
        ProfitLossResponse result = financialService.calculateProfitLoss(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(5000.0, result.getTotalIncome());
        assertEquals(3000.0, result.getTotalExpenses());
        assertEquals(2000.0, result.getNetProfit());
        assertEquals(40.0, result.getProfitMargin(), 0.01);
    }

    @Test
    void calculateProfitLoss_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        
        when(transactionRepository.sumByTypeAndDateBetween(
                FinancialTransaction.TransactionType.INCOME, start, end))
                .thenReturn(null);
        when(transactionRepository.sumByTypeAndDateBetween(
                FinancialTransaction.TransactionType.EXPENSE, start, end))
                .thenReturn(null);

        // Act
        ProfitLossResponse result = financialService.calculateProfitLoss(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getTotalIncome());
        assertEquals(0.0, result.getTotalExpenses());
        assertEquals(0.0, result.getNetProfit());
    }

    @Test
    void getIncomeBreakdown_ShouldReturnBreakdown() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        
        List<Object[]> mockResults = List.of(
                new Object[]{"MILK_SALES", 3000.0, 10L},
                new Object[]{"LIVESTOCK_SALES", 2000.0, 5L}
        );
        
        when(transactionRepository.getBreakdownByCategory(
                FinancialTransaction.TransactionType.INCOME, start, end))
                .thenReturn(mockResults);

        // Act
        List<CategoryBreakdownResponse> result = financialService.getIncomeBreakdown(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("MILK_SALES", result.get(0).getCategory());
        assertEquals(3000.0, result.get(0).getTotal());
        assertEquals(60.0, result.get(0).getPercentage(), 0.01);
        assertEquals(10, result.get(0).getTransactionCount());
    }

    @Test
    void getExpenseBreakdown_ShouldReturnBreakdown() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        
        List<Object[]> mockResults = List.of(
                new Object[]{"FEED", 1500.0, 8L},
                new Object[]{"MEDICINE", 500.0, 3L}
        );
        
        when(transactionRepository.getBreakdownByCategory(
                FinancialTransaction.TransactionType.EXPENSE, start, end))
                .thenReturn(mockResults);

        // Act
        List<CategoryBreakdownResponse> result = financialService.getExpenseBreakdown(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("FEED", result.get(0).getCategory());
        assertEquals(1500.0, result.get(0).getTotal());
    }

    @Test
    void getFinancialTrends_ShouldReturnMonthlyTrends() {
        // Arrange
        LocalDate start = LocalDate.now().minusMonths(3);
        LocalDate end = LocalDate.now();
        
        List<Object[]> mockResults = List.of(
                new Object[]{2024, 1, 5000.0, 3000.0},
                new Object[]{2024, 2, 6000.0, 3500.0}
        );
        
        when(transactionRepository.getMonthlyTrends(start, end))
                .thenReturn(mockResults);

        // Act
        List<MonthlyTrendDTO> result = financialService.getFinancialTrends(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5000.0, result.get(0).getTotalIncome());
        assertEquals(3000.0, result.get(0).getTotalExpenses());
        assertEquals(2000.0, result.get(0).getNetProfit());
    }
}
