package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.model.ItemPedido;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoDTOResponse {
    private Long id;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private String enderecoEntrega;
    private List<ItemPedido> itens;

    private String nomeCliente;
    private String nomeRestaurante;
}
