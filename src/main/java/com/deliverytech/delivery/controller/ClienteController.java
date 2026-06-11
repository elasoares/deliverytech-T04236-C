package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.response.ClienteDTORequest;
import com.deliverytech.delivery.dto.request.response.ClienteDTOResponse;
import com.deliverytech.delivery.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service){
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteDTOResponse> cadastrarCliente(@Valid @RequestBody ClienteDTORequest dto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.cadastrarCliente(dto));
    }

    @GetMapping("/{id}")
    public ClienteDTOResponse buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }


}
