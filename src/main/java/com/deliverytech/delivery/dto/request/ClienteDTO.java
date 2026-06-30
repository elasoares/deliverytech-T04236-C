package com.deliverytech.delivery.dto.request;

import com.deliverytech.delivery.validation.TelefoneValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Dados para cadastro/atualização do cliente.")
public class ClienteDTO {

    @Schema(description = "Telefone do cliente", example = "(11)91234-1234", required = true)
    @NotBlank(message = "Telefone é obrigatório")
    @TelefoneValido
    private String telefone;

    @Schema(description = "Endereço do cliente", example = "Rua A, 123",  minLength = 5, required = true)
    @Size(min = 5, message = "Endereço deve ter no mínimo 5 caracteres.")
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
}
