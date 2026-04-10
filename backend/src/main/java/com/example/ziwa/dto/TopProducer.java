package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Top producer DTO for dashboard
 * Requirements: 12.6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProducer {
    private Integer rank;
    private Long cowId;
    private String cowTagId;
    private Double totalProduction;
    private Integer recordCount;
}
