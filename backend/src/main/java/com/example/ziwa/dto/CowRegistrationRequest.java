package com.example.ziwa.dto;

import com.example.ziwa.model.Cow.CowStatus;
import com.example.ziwa.validation.UniqueCowTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
@Schema(description = "Request to register a new cow")
public class CowRegistrationRequest {
    
    @NotBlank(message = "Tag ID is required")
    @Size(min = 1, max = 50, message = "Tag ID must be between 1 and 50 characters")
    @UniqueCowTag
    @Schema(description = "Unique identifier tag for the cow", example = "COW-001", required = true)
    private String tagId;
    
    @NotBlank(message = "Breed is required")
    @Size(max = 100, message = "Breed must not exceed 100 characters")
    @Schema(description = "Breed of the cow", example = "Holstein", required = true)
    private String breed;
    
    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    @Schema(description = "Date of birth of the cow", example = "2020-01-15", required = true)
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Acquisition date is required")
    @PastOrPresent(message = "Acquisition date cannot be in the future")
    @Schema(description = "Date when the cow was acquired", example = "2021-03-20", required = true)
    private LocalDate acquisitionDate;
    
    @Builder.Default
    @Schema(description = "Current status of the cow", example = "ACTIVE", defaultValue = "ACTIVE")
    private CowStatus status = CowStatus.ACTIVE;
}
