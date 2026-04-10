package com.example.ziwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Upcoming vaccination DTO
 * Requirements: 12.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingVaccination {
    private Long cowId;
    private String cowTagId;
    private LocalDate scheduledDate;
    private String vaccinationType;
    private String notes;
}
