package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.model.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockUsuarioSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUsuario> {

    @Override
    public SecurityContext createSecurityContext(WithMockUsuario annotation) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(annotation.email());
        usuario.setNome("Teste");
        usuario.setSenha("senha123");
        usuario.setRole(Role.valueOf(annotation.role()));

        var auth = new UsernamePasswordAuthenticationToken(
                usuario,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + annotation.role()))
        );

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        return ctx;
    }
}