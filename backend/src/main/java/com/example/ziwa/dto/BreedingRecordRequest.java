package com.example.ziwa.dto;

import com.example.ziwa.validation.DateNotFuture;
import jakarta.validation.constraints.NotNull;
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
public class BreedingRecordRequest {
    
    @NotNull(message = "Breeding date is required")
    @DateNotFuture
    private LocalDate breedingDate;
    
    @Size(max = 50, message = "Bull ID must not exceed 50 characters")
    private String bullId;
    
    private LocalDate expectedCalvingDate;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
