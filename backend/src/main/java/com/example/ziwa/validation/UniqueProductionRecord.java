package com.example.ziwa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueProductionRecordValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueProductionRecord {
    String message() default "Production record already exists for this cow and date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
