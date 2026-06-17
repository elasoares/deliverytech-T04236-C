package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.response.ClienteDTORequest;
import com.deliverytech.delivery.dto.request.response.ClienteDTOResponse;
import com.deliverytech.delivery.dto.request.response.PagedResponse;
import com.deliverytech.delivery.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes.")
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

    @Operation(summary = "Buscar cliente por Id.")
    @ApiResponses(
          value = {
                 @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso."),
                 @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo Id mencionado.")
          }
    )
    @GetMapping("/{id}")
    public ResponseEntity<com.deliverytech.delivery.dto.request.response.ApiResponse<ClienteDTOResponse>> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(new com.deliverytech.delivery.dto.request.response.ApiResponse<>(service.buscarPorId(id)));
    }

    @Operation(summary = "Buscar cliente por e-mail.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo Id mencionado.")
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteDTOResponse> buscarPorEmail(@PathVariable String email){
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTOResponse> atualizarCliente(@PathVariable Long id, @RequestBody ClienteDTORequest dto){
        return ResponseEntity.ok(service.atualizarCliente(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    public ClienteDTOResponse toggle(@PathVariable Long id){
        return service.toggle(id);
    }

    @Operation(summary = "Listar clientes ativos.")
    @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200",
                          description = "Lista de clientes ativos retornado com sucesso."),
                  @ApiResponse(responseCode = "200", description = "[]")
          }
    )
    @GetMapping("/listar")
    public ResponseEntity<PagedResponse<ClienteDTOResponse>> listarClientesAtivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.listarClientesAtivos(pageable);
        var response = new PagedResponse<>(pageResult);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(response);
    }

}
