package com.example.ziwa.dto;

import com.example.ziwa.model.FinancialTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private LocalDate date;
    private FinancialTransaction.TransactionType type;
    private String category;
    private Double amount;
    private String description;
    private String referenceId;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TransactionResponse fromEntity(FinancialTransaction transaction) {
        return TransactionResponse.builder()
            .id(transaction.getId())
            .date(transaction.getDate())
            .type(transaction.getType())
            .category(transaction.getCategory())
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .referenceId(transaction.getReferenceId())
            .deleted(transaction.getDeleted())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .build();
    }
}
