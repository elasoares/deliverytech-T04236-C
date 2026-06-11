package com.deliverytech.delivery.dto.request.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ClienteDTORequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 5, message = "Nome deve ter no mínimo 5 caracteres.")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @Size(min = 5, message = "Endereço deve ter no mínimo 5 caracteres.")
    private String endereco;
}
