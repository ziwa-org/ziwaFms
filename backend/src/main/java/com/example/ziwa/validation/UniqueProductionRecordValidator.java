package com.example.ziwa.validation;

import com.example.ziwa.dto.ProductionRecordRequest;
import com.example.ziwa.repository.MilkProductionRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueProductionRecordValidator implements ConstraintValidator<UniqueProductionRecord, ProductionRecordRequest> {
    
    private final MilkProductionRepository milkProductionRepository;

    @Override
    public boolean isValid(ProductionRecordRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getCowId() == null || request.getDate() == null) {
            return true; // Let other validators handle null values
        }
        return milkProductionRepository.findByCowIdAndDate(request.getCowId(), request.getDate()).isEmpty();
    }
}
