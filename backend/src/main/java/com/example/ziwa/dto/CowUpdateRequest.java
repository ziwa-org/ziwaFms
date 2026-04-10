package com.example.ziwa.dto;

import com.example.ziwa.model.Cow.CowStatus;
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
public class CowUpdateRequest {
    
    @Size(min = 1, max = 50, message = "Tag ID must be between 1 and 50 characters")
    private String tagId;
    
    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;
    
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    
    @PastOrPresent(message = "Acquisition date cannot be in the future")
    private LocalDate acquisitionDate;
    
    private CowStatus status;
}
