package com.example.marketPlace.api.manager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow null values, use @NotNull if needed
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
