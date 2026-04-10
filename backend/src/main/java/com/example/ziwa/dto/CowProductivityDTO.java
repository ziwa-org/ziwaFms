package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CowProductivityDTO {
    
    private Long cowId;
    private String cowTagId;
    private Double averageProduction;
}
