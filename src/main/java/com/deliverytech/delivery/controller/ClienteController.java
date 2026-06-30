package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteDTO;
import com.deliverytech.delivery.dto.request.ClienteDTOAtualizar;
import com.deliverytech.delivery.dto.response.ClienteDTOResponse;
import com.deliverytech.delivery.dto.response.PagedResponse;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes.")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados."),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado com esse e-mail.")
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteDTOResponse> cadastrarCliente(
            @Valid @RequestBody ClienteDTO dto, @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.cadastrarCliente(dto, logado));
    }

    @Operation(summary = "Buscar cliente por Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClienteDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo Id mencionado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.deliverytech.delivery.dto.response.ApiResponse<ClienteDTOResponse>> buscarPorId(
            @Parameter(
                    description = "Id do cliente a ser buscado.",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(new com.deliverytech.delivery.dto.response.ApiResponse<>(service.buscarPorId(id)));
    }

/*    @Operation(summary = "Buscar cliente por e-mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo e-mail mencionado.")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteDTOResponse> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }*/

    @Operation(summary = "Atualizar dados do cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo Id mencionado.")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE'))")
    @PutMapping("/atualizar")
    public ResponseEntity<ClienteDTOResponse> atualizarCliente(@AuthenticationPrincipal Usuario usuarioLogado, @Valid @RequestBody ClienteDTOAtualizar dto) {
        return ResponseEntity.ok(service.atualizarCliente(usuarioLogado, dto));
    }

    @Operation(summary = "Ativar ou desativar cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do cliente alterado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo Id mencionado.")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE'))")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ClienteDTOResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }

    @Operation(summary = "Listar clientes ativos.", description = "Retorna todos os clientes ativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes ativos retornada com sucesso.")
    })
    @GetMapping("/listar")
    public ResponseEntity<PagedResponse<ClienteDTOResponse>> listarClientesAtivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.listarClientesAtivos(pageable);
        var response = new PagedResponse<>(pageResult);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(response);
    }
}