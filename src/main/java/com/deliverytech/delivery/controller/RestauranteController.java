package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteDTO;
import com.deliverytech.delivery.dto.response.PagedResponse;
import com.deliverytech.delivery.dto.response.RestauranteDTOResponse;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(
        value = "/api/restaurantes",
        produces = "application/json"
)
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de estabelecimentos.")
public class RestauranteController {

    @Autowired
    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados."),
            @ApiResponse(responseCode = "409", description = "Conflito: Nome de restaurante já existente.")
    })
    @PostMapping
    public ResponseEntity<com.deliverytech.delivery.dto.response.ApiResponse<RestauranteDTOResponse>> cadastrar(
            @Valid @RequestBody RestauranteDTO dados, @AuthenticationPrincipal Usuario logado) {
        RestauranteDTOResponse response = service.cadastrar(dados, logado);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location)
                .header("Content-Type", "application/json")
                .body(new com.deliverytech.delivery.dto.response.ApiResponse<>(response));
    }

    @Operation(summary = "Listar restaurantes ativos (paginado).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada com sucesso.")
    })
    @GetMapping("/listar")
    public ResponseEntity<PagedResponse<RestauranteDTOResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.listarAtivos(pageable);
        var response = new PagedResponse<>(pageResult);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(response);
    }

    @Operation(summary = "Buscar restaurante por Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.deliverytech.delivery.dto.response.ApiResponse<RestauranteDTOResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(new com.deliverytech.delivery.dto.response.ApiResponse<>(service.buscarPorId(id)));
    }

    @Operation(summary = "Buscar restaurantes por categoria (paginado).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso.")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE'))")
    @GetMapping("/categoria")
    public ResponseEntity<PagedResponse<RestauranteDTOResponse>> buscarPorCategoria(
            @RequestParam String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.buscarPorCategoria(categoria, pageable);
        var response = new PagedResponse<>(pageResult);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Ativar ou desativar restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<com.deliverytech.delivery.dto.response.ApiResponse<RestauranteDTOResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(new com.deliverytech.delivery.dto.response.ApiResponse<>(service.toggle(id)));
    }
}
