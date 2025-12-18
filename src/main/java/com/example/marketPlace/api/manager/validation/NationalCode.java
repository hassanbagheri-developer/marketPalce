package com.example.marketPlace.api.manager.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NationalCodeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NationalCode {
    String message() default "Invalid national code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
