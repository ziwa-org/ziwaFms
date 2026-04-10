package com.example.ziwa.dto;

import com.example.ziwa.validation.UniqueProductionRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueProductionRecord
public class ProductionRecordRequest {
    
    @NotNull(message = "Cow ID is required")
    private Long cowId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Morning quantity is required")
    @PositiveOrZero(message = "Morning quantity must be zero or positive")
    private Double morningQuantity;
    
    @NotNull(message = "Evening quantity is required")
    @PositiveOrZero(message = "Evening quantity must be zero or positive")
    private Double eveningQuantity;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
