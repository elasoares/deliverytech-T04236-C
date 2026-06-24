package com.deliverytech.delivery.security;

import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UsuarioRepository repository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioRepository repository) {
        this.jwtUtil = jwtUtil;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String token = extractToken(request);

        if (token != null) {
            try {

                String email = jwtUtil.extractUsername(token);
                System.out.println("Email extraído: " + email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    Usuario usuario = repository.findByEmail(email).orElse(null);
                    System.out.println("Usuário encontrado: " + usuario);

                    if (usuario != null && jwtUtil.validateToken(token, usuario.getEmail())) {
                        boolean valido = jwtUtil.validateToken(token, usuario.getEmail());
                        System.out.println("Token válido: " + valido);
                        String role = jwtUtil.extractRole(token);

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                List.of(authority)
                        );

                        auth.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro JWT " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}