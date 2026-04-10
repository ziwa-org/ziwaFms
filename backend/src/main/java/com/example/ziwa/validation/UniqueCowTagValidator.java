package com.example.ziwa.validation;

import com.example.ziwa.repository.CowRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCowTagValidator implements ConstraintValidator<UniqueCowTag, String> {
    
    private final CowRepository cowRepository;

    @Override
    public boolean isValid(String tagId, ConstraintValidatorContext context) {
        if (tagId == null || tagId.trim().isEmpty()) {
            return true; // Let @NotNull/@NotBlank handle null/empty validation
        }
        return !cowRepository.existsByTagId(tagId);
    }
}
