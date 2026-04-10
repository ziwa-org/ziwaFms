package com.example.ziwa.dto;

import com.example.ziwa.model.Cow.CowStatus;
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
public class CowResponse {
    
    private Long id;
    
    private String tagId;
    
    private String breed;
    
    private LocalDate dateOfBirth;
    
    private LocalDate acquisitionDate;
    
    private CowStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
