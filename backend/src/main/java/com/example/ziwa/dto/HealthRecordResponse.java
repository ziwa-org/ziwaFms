package com.example.ziwa.dto;

import com.example.ziwa.model.HealthRecord.HealthRecordType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordResponse {
    private Long id;
    private Long cowId;
    private String cowTagId;
    private LocalDate date;
    private HealthRecordType recordType;
    private String description;
    private String veterinarianName;
    private String medication;
    private Integer withdrawalPeriodDays;
    private LocalDate withdrawalEndDate;
    private Double cost;
    private LocalDateTime createdAt;
}
