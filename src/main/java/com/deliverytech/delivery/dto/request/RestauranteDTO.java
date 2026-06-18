package com.deliverytech.delivery.dto.request;

import java.math.BigDecimal;

import com.deliverytech.delivery.enums.CategoriaRestaurante;
import com.deliverytech.delivery.validation.CategoriaValida;
import com.deliverytech.delivery.validation.TelefoneValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para cadastro/atualização de restaurante.")
public class RestauranteDTO {

    @Schema(description = "Nome do restaurante.", example = "Pizzaria Bella Itália")
    @NotBlank(message = "Nome do restaurante é obrigatório")
    private String nome;

    @Schema(description = "Categoria do restaurante.", example = "Italiana")
    @CategoriaValida
    private String categoria;

    @Schema(description = "Endereço do restaurante.", example = "Rua Central, 500")
    @Size(min = 5, max = 255, message = "Endereço deve ter entre 5 e 255 caracteres")
    private String endereco;

    @Schema(description = "Telefone do restaurante.", example = "(11) 99999-9999")
    @TelefoneValido
    private String telefone;

    @Schema(description = "Taxa de entrega do restaurante.", example = "10.00")
    @NotNull(message = "A taxa de entrega é obrigatória")
    private BigDecimal taxaEntrega;
}
