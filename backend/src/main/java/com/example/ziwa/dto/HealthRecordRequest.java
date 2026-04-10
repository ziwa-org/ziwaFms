package com.example.ziwa.dto;

import com.example.ziwa.model.HealthRecord.HealthRecordType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordRequest {
    @NotNull(message = "Cow ID is required")
    private Long cowId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Record type is required")
    private HealthRecordType recordType;

    @NotBlank(message = "Description is required")
    private String description;

    private String veterinarianName;

    private String medication;

    @Min(value = 0, message = "Withdrawal period days must be non-negative")
    private Integer withdrawalPeriodDays;

    @Min(value = 0, message = "Cost must be non-negative")
    private Double cost;
}
