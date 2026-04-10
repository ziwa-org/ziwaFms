package com.example.ziwa.controller;

import com.example.ziwa.dto.*;
import com.example.ziwa.model.FinancialTransaction;
import com.example.ziwa.service.FinancialService;
import com.example.ziwa.util.PageRequestBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/financial")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Financial Management", description = "Endpoints for managing financial transactions and analytics")
@SecurityRequirement(name = "bearerAuth")
public class FinancialController {
    private final FinancialService financialService;

    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
        @Valid @RequestBody TransactionRequest request
    ) {
        TransactionResponse response = financialService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<PagedResponse<TransactionResponse>> listTransactions(
        @RequestParam(required = false) FinancialTransaction.TransactionType type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortDirection
    ) {
        // Default sort by date descending if not specified
        String effectiveSortBy = (sortBy != null) ? sortBy : "date";
        String effectiveSortDirection = (sortDirection != null) ? sortDirection : "DESC";
        
        Pageable pageable = PageRequestBuilder.build(page, size, effectiveSortBy, effectiveSortDirection);
        
        Page<TransactionResponse> transactions = financialService.listTransactions(
            type, category, start, end, pageable
        );
        return ResponseEntity.ok(PagedResponse.of(transactions));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse response = financialService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
        @PathVariable Long id,
        @Valid @RequestBody TransactionRequest request
    ) {
        TransactionResponse response = financialService.updateTransaction(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        financialService.softDeleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/profit-loss")
    public ResponseEntity<ProfitLossResponse> getProfitLoss(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        ProfitLossResponse response = financialService.calculateProfitLoss(start, end);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/income-breakdown")
    public ResponseEntity<List<CategoryBreakdownResponse>> getIncomeBreakdown(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<CategoryBreakdownResponse> breakdown = financialService.getIncomeBreakdown(start, end);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/analytics/expense-breakdown")
    public ResponseEntity<List<CategoryBreakdownResponse>> getExpenseBreakdown(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<CategoryBreakdownResponse> breakdown = financialService.getExpenseBreakdown(start, end);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/analytics/trends")
    public ResponseEntity<List<MonthlyTrendDTO>> getFinancialTrends(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<MonthlyTrendDTO> trends = financialService.getFinancialTrends(start, end);
        return ResponseEntity.ok(trends);
    }
}
