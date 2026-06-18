package com.deliverytech.delivery.validation;

import com.deliverytech.delivery.validation.validor.CategoriaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoriaValidator.class)
public @interface CategoriaValida {
    String message() default "Categoria é obrigatória";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
