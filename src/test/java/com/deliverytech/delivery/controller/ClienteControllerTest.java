package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteDTO;
import com.deliverytech.delivery.dto.request.ClienteDTOAtualizar;
import com.deliverytech.delivery.dto.response.ClienteDTOResponse;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTOResponse clienteResponse;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteResponse = new ClienteDTOResponse();

        clienteDTO = new ClienteDTO();
        clienteDTO.setTelefone("(85)99999-9999");
        clienteDTO.setEndereco("Rua Teste, 123");
    }


    @Test
    @WithMockUsuario
    @DisplayName("Deve cadastrar cliente e retornar 201")
    void deveCadastrarClienteComSucesso() throws Exception {
        when(clienteService.cadastrarCliente(any(), any()))
                .thenReturn(clienteResponse);

        mockMvc.perform(post("/api/clientes/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUsuario
    @DisplayName("Deve retornar 400 ao cadastrar com telefone inválido")
    void deveRetornar400AoCadastrarComTelefoneInvalido() throws Exception {
        ClienteDTO dtoInvalido = new ClienteDTO();
        dtoInvalido.setTelefone("");
        dtoInvalido.setEndereco("Rua Teste, 123");

        mockMvc.perform(post("/api/clientes/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUsuario
    @DisplayName("Deve retornar 409 ao cadastrar cliente duplicado")
    void deveRetornar409AoCadastrarClienteDuplicado() throws Exception {
        when(clienteService.cadastrarCliente(any(), any()))
                .thenThrow(new BusinessException("Cliente já cadastrado para este usuário."));

        mockMvc.perform(post("/api/clientes/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUsuario
    @DisplayName("Deve atualizar cliente e retornar 200")
    void deveAtualizarClienteComSucesso() throws Exception {
        ClienteDTOAtualizar atualizarDTO = new ClienteDTOAtualizar();
        atualizarDTO.setNome("Teste");
        atualizarDTO.setEmail("teste@gmail.com");
        atualizarDTO.setEndereco("Rua A, 123");
        atualizarDTO.setTelefone("(11)91234-1234");

        when(clienteService.atualizarCliente(any(), any(ClienteDTOAtualizar.class)))
                .thenReturn(clienteResponse);

        mockMvc.perform(put("/api/clientes/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUsuario
    @DisplayName("Deve retornar 404 ao atualizar cliente inexistente")
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        ClienteDTOAtualizar atualizarDTO = new ClienteDTOAtualizar();
        atualizarDTO.setNome("Teste");
        atualizarDTO.setEmail("teste@gmail.com");
        atualizarDTO.setEndereco("Rua A, 123");
        atualizarDTO.setTelefone("(11)91234-1234");

        when(clienteService.atualizarCliente(any(), any(ClienteDTOAtualizar.class)))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado."));

        mockMvc.perform(put("/api/clientes/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUsuario(role = "ADMIN")
    @DisplayName("Deve listar clientes ativos e retornar 200")
    void deveListarClientesAtivos() throws Exception {
        var page = new org.springframework.data.domain.PageImpl<>(
                java.util.List.of(clienteResponse),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1
        );

        when(clienteService.listarClientesAtivos(any())).thenReturn(page);

        mockMvc.perform(get("/api/clientes/listar")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUsuario(role = "ADMIN")
    @DisplayName("Deve buscar cliente por ID e retornar 200")
    void deveBuscarClientePorId() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(clienteResponse);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUsuario(role = "ADMIN")
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente")
    void deveRetornar404AoBuscarClienteInexistente() throws Exception {
        when(clienteService.buscarPorId(99L))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado."));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUsuario(role = "ADMIN")
    @DisplayName("Deve inativar cliente e retornar 200")
    void deveInativarCliente() throws Exception {
        when(clienteService.toggle(1L)).thenReturn(clienteResponse);

        mockMvc.perform(patch("/api/clientes/1/toggle"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUsuario(role = "ADMIN")
    @DisplayName("Deve retornar 404 ao inativar cliente inexistente")
    void deveRetornar404AoInativarClienteInexistente() throws Exception {
        when(clienteService.toggle(99L))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado."));

        mockMvc.perform(patch("/api/clientes/99/toggle"))
                .andExpect(status().isNotFound());
    }
}