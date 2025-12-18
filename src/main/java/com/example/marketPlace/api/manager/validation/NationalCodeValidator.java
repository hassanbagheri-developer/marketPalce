package com.example.marketPlace.api.manager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;


public class NationalCodeValidator implements
        ConstraintValidator<NationalCode, String> {

    @Override
    public void initialize(NationalCode constraintAnnotation) {

    }

    @Override
    public boolean isValid(String nId, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(nId))
            return false;
        if (nId.length() != 10)
            return false;
        int controlNumber = Character.getNumericValue(nId.charAt(9));
        int sum = 0;
        int j = 10;
        for (int i = 0 ; i < 9 ; i++){
            sum += j * Character.getNumericValue(nId.charAt(i));
            j--;
        }
        int r = sum % 11;
        if (r < 2){
            return r == controlNumber;
        }else {
            return controlNumber == (11 - r);
        }
    }
}
