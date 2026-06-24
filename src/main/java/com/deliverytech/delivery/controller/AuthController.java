package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.LoginRequestDTO;
import com.deliverytech.delivery.dto.request.RegisterRequestDTO;
import com.deliverytech.delivery.dto.response.LoginDTOResponse;
import com.deliverytech.delivery.dto.response.UsuarioDTOResponse;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginDTOResponse> cadastrar(@RequestBody RegisterRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTOResponse> logar(@RequestBody LoginRequestDTO dto) {
    return ResponseEntity.ok(service.logar(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTOResponse> me(Authentication auth){
        Usuario usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(
                new UsuarioDTOResponse(
                       usuario.getId(),
                       usuario.getEmail(),
                       usuario.getNome(),
                       usuario.getRole()
                )
        );
    }

}
