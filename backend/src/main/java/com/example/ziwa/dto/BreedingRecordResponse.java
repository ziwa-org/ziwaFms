package com.example.ziwa.dto;

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
public class BreedingRecordResponse {
    
    private Long id;
    
    private Long cowId;
    
    private String cowTagId;
    
    private LocalDate breedingDate;
    
    private String bullId;
    
    private LocalDate expectedCalvingDate;
    
    private LocalDate actualCalvingDate;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
