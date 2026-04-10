package com.example.ziwa.controller;

import com.example.ziwa.dto.*;
import com.example.ziwa.model.FinancialTransaction;
import com.example.ziwa.service.FinancialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialController.class)
@WithMockUser
class FinancialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinancialService financialService;

    private TransactionRequest testRequest;
    private TransactionResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TransactionRequest.builder()
                .date(LocalDate.now())
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("MILK_SALES")
                .amount(1000.0)
                .description("Milk sales for the week")
                .referenceId("REF001")
                .build();

        testResponse = TransactionResponse.builder()
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
    }

    @Test
    void createTransaction_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        when(financialService.createTransaction(any(TransactionRequest.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/financial/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.category").value("MILK_SALES"));

        verify(financialService).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void createTransaction_WithNegativeAmount_ShouldReturn400() throws Exception {
        // Arrange
        testRequest.setAmount(-100.0);

        // Act & Assert
        mockMvc.perform(post("/api/financial/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verify(financialService, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void createTransaction_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Arrange
        testRequest.setDescription(null);

        // Act & Assert
        mockMvc.perform(post("/api/financial/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verify(financialService, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void listTransactions_ShouldReturnPagedResults() throws Exception {
        // Arrange
        Page<TransactionResponse> page = new PageImpl<>(List.of(testResponse));
        when(financialService.listTransactions(any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/financial/transactions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(financialService).listTransactions(any(), any(), any(), any(), any());
    }

    @Test
    void listTransactions_WithTypeFilter_ShouldFilterByType() throws Exception {
        // Arrange
        Page<TransactionResponse> page = new PageImpl<>(List.of(testResponse));
        when(financialService.listTransactions(any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/financial/transactions")
                        .param("type", "INCOME")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(financialService).listTransactions(
                eq(FinancialTransaction.TransactionType.INCOME), 
                any(), any(), any(), any());
    }

    @Test
    void getTransactionById_WhenExists_ShouldReturn200() throws Exception {
        // Arrange
        when(financialService.getTransactionById(1L)).thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(get("/api/financial/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1000.0));

        verify(financialService).getTransactionById(1L);
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturn200() throws Exception {
        // Arrange
        when(financialService.updateTransaction(eq(1L), any(TransactionRequest.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(put("/api/financial/transactions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(financialService).updateTransaction(eq(1L), any(TransactionRequest.class));
    }

    @Test
    void deleteTransaction_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(financialService).softDeleteTransaction(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/financial/transactions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(financialService).softDeleteTransaction(1L);
    }

    @Test
    void getProfitLoss_WithValidDateRange_ShouldReturn200() throws Exception {
        // Arrange
        ProfitLossResponse profitLoss = ProfitLossResponse.builder()
                .startDate(LocalDate.now().minusDays(30))
                .endDate(LocalDate.now())
                .totalIncome(5000.0)
                .totalExpenses(3000.0)
                .netProfit(2000.0)
                .profitMargin(40.0)
                .build();

        when(financialService.calculateProfitLoss(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(profitLoss);

        // Act & Assert
        mockMvc.perform(get("/api/financial/analytics/profit-loss")
                        .param("start", LocalDate.now().minusDays(30).toString())
                        .param("end", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(5000.0))
                .andExpect(jsonPath("$.totalExpenses").value(3000.0))
                .andExpect(jsonPath("$.netProfit").value(2000.0));

        verify(financialService).calculateProfitLoss(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getIncomeBreakdown_ShouldReturnBreakdown() throws Exception {
        // Arrange
        List<CategoryBreakdownResponse> breakdown = List.of(
                CategoryBreakdownResponse.builder()
                        .category("MILK_SALES")
                        .total(3000.0)
                        .percentage(60.0)
                        .transactionCount(10)
                        .build()
        );

        when(financialService.getIncomeBreakdown(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(breakdown);

        // Act & Assert
        mockMvc.perform(get("/api/financial/analytics/income-breakdown")
                        .param("start", LocalDate.now().minusDays(30).toString())
                        .param("end", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("MILK_SALES"))
                .andExpect(jsonPath("$[0].total").value(3000.0));

        verify(financialService).getIncomeBreakdown(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getExpenseBreakdown_ShouldReturnBreakdown() throws Exception {
        // Arrange
        List<CategoryBreakdownResponse> breakdown = List.of(
                CategoryBreakdownResponse.builder()
                        .category("FEED")
                        .total(1500.0)
                        .percentage(75.0)
                        .transactionCount(8)
                        .build()
        );

        when(financialService.getExpenseBreakdown(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(breakdown);

        // Act & Assert
        mockMvc.perform(get("/api/financial/analytics/expense-breakdown")
                        .param("start", LocalDate.now().minusDays(30).toString())
                        .param("end", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("FEED"))
                .andExpect(jsonPath("$[0].total").value(1500.0));

        verify(financialService).getExpenseBreakdown(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getFinancialTrends_ShouldReturnMonthlyTrends() throws Exception {
        // Arrange
        List<MonthlyTrendDTO> trends = List.of(
                MonthlyTrendDTO.builder()
                        .month(YearMonth.of(2024, 1))
                        .totalIncome(5000.0)
                        .totalExpenses(3000.0)
                        .netProfit(2000.0)
                        .build()
        );

        when(financialService.getFinancialTrends(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(trends);

        // Act & Assert
        mockMvc.perform(get("/api/financial/analytics/trends")
                        .param("start", LocalDate.now().minusMonths(3).toString())
                        .param("end", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalIncome").value(5000.0))
                .andExpect(jsonPath("$[0].netProfit").value(2000.0));

        verify(financialService).getFinancialTrends(any(LocalDate.class), any(LocalDate.class));
    }
}
