package com.deliverytech.delivery.validation.validor;

import com.deliverytech.delivery.validation.TelefoneValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<TelefoneValido, String> {
      @Override
    public boolean isValid(String value, ConstraintValidatorContext contex){
        if(value == null || value.isBlank()) return false;
        return value.matches("\\(\\d{2}\\)[\\s-]?\\d{4,5}-?\\d{4}") || value.matches("\\d{2}[\\s-]?\\d{4,5}-?\\d{4}");
    }
}
