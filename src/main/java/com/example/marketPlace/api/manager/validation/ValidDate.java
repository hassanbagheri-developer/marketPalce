package com.example.marketPlace.api.manager.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Invalid date format, expected yyyy-MM-dd HH:mm:ss";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
