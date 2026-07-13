package com.deliverytech.delivery.controller;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUsuarioSecurityContextFactory.class)
public @interface WithMockUsuario {
    String email() default "teste@gmail.com";
    String role() default "CLIENTE";
}