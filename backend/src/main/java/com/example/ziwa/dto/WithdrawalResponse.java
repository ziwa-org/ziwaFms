package com.example.ziwa.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponse {
    private Long cowId;
    private String cowTagId;
    private Long healthRecordId;
    private LocalDate withdrawalEndDate;
    private Integer daysRemaining;
    private String medication;
}
