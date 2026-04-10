package com.example.ziwa.dto;

import com.example.ziwa.model.FinancialTransaction;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Transaction type is required")
    private FinancialTransaction.TransactionType type;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String referenceId;
}
