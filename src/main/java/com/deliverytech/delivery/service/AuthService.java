package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.LoginRequestDTO;
import com.deliverytech.delivery.dto.request.RegisterRequestDTO;
import com.deliverytech.delivery.dto.response.LoginDTOResponse;
import com.deliverytech.delivery.dto.response.UsuarioDTOResponse;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.UsuarioRepository;
import com.deliverytech.delivery.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository repository, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginDTOResponse  cadastrar(RegisterRequestDTO dto){
        if(repository.existsByEmail(dto.getEmail())){
            throw new BusinessException("E-mail já cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(encoder.encode(dto.getSenha()));
        usuario.setRole(dto.getRole() != null ? dto.getRole() : Role.CLIENTE);
        repository.save(usuario);

        return construirReposta(usuario);
    }

    public LoginDTOResponse logar(LoginRequestDTO dto){
        Usuario usuario = repository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new BusinessException("Credenciais inválidas."));

        if(!encoder.matches(dto.getSenha(), usuario.getSenha())){
            throw new BusinessException("Credenciais inválidas.");
        }
        return construirReposta(usuario);
    }

    private LoginDTOResponse construirReposta(Usuario usuario){
        String token = jwtUtil.generateToken(usuario);

        UsuarioDTOResponse reposta = new UsuarioDTOResponse(
               usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getRole()
        );
        return new LoginDTOResponse(token, "Bearer", System.currentTimeMillis() + 86400000L, reposta);
    }
}
