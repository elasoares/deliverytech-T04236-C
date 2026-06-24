package com.deliverytech.delivery.dto.response;

import lombok.*;

@Getter
@Setter
public class LoginDTOResponse {
    private String token;
    private String tipo;
    private Long expiracao;
    private UsuarioDTOResponse usuario;

    public LoginDTOResponse(String token, String tipo, Long expiracao, UsuarioDTOResponse usuario){
        this.token = token;
        this.tipo = "Bearer";
        this.expiracao = expiracao;
        this.usuario = usuario;
    }

}
