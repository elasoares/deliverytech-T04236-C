package com.deliverytech.delivery.validation;

import com.deliverytech.delivery.validation.validor.TelefoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelefoneValidator.class)
public @interface TelefoneValido {

    String message() default "Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou similar";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
