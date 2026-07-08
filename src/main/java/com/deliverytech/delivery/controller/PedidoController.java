package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.response.PedidoDTOResponse;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.service.AuditoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery.dto.request.PedidoDTO;

import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.service.PedidoService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService service;
    private final AuditoriaService auditoria;

    public PedidoController(PedidoService service, AuditoriaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @PreAuthorize("hasRole('CLIENTE') or (hasRole('RESTAURANTE'))")
    @GetMapping("/meus")
    public ResponseEntity<?> meusPedidos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(service.meusPedidos(usuarioLogado, pageable));
    }


    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody @Valid PedidoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var pedido = service.criarPedido(dto, usuarioLogado);

        auditoria.registrar(
                "PEDIDO_CRIADO",
                usuarioLogado.getEmail(),
                "pedido: " + pedido.getId(),
                "Pedido criado no restaurante " + dto.getRestauranteId()
        );
        return ResponseEntity.status(201)
                .body(pedido);
    }


    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var pedido = service.cancelarPedido(id, usuarioLogado);
        auditoria.registrar(
                "PEDIDO_CANCELADO",
                usuarioLogado.getEmail(),
                "pedido: " + id,
                "Pedido cancelado pelo usuário "
        );
        return ResponseEntity.ok(pedido);
    }

    /*@PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE'))")*/
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTOResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido novoStatus,
            @AuthenticationPrincipal Usuario usuarioLogado
            ){

            return ResponseEntity.ok(service.atualizarStatus(id, novoStatus, usuarioLogado));
    }

}
