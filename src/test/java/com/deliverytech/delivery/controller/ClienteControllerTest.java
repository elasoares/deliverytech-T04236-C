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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SpringBootTest
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteRequest;
    private ClienteDTOResponse clienteResponse;

    @BeforeEach
    void setUp(){
        clienteResponse = new ClienteDTOResponse();

        clienteRequest = new ClienteDTO();
        clienteRequest.setTelefone("(11)91234-1234");
        clienteRequest.setEndereco("Rua Teste, 123");
    }

    @Test
    @DisplayName("Deve cadastrar cliente e retornar 201")
    @WithMockUser(roles = "CLIENTE")
    void deveCadastrarClienteComSucesso() throws Exception {
        when(clienteService.cadastrarCliente(any(), any()))
                .thenReturn(clienteResponse);

        mockMvc.perform(post("/api/clientes/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar cliente com telefone inválido")
    @WithMockUser(roles = "CLIENTE")
    void deveRetornar400aoCadastrarComTelefoneInvalido() throws Exception{
        ClienteDTO dtoInvalido = new ClienteDTO();
        dtoInvalido.setTelefone("");
        dtoInvalido.setEndereco("Rua Teste, 123");

        mockMvc.perform(post("/api/clientes/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Deve retornar 409 ao cadastrar cliente já existente para usuário")
    @WithMockUser(roles = "CLIENTE")
    void deveRetornar409aoCadastrarClienteDuplicado() throws Exception{
        when(clienteService.cadastrarCliente(any(), any()))
                .thenThrow(new BusinessException("Cliente já cadastrado para este usuário."));

        mockMvc.perform(post("/api/clientes/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isConflict());

    }

    @Test
    @DisplayName("Deve listar clientes com paginação padrão")
    @WithMockUser(roles = "ADMIN")
    void deveListarClientesComPaginacaoPadrao() throws Exception{
        var page = new PageImpl<>(List.of(clienteResponse));

        when(clienteService.listarClientesAtivos(any())).thenReturn(page);

        mockMvc.perform(get("/api/clientes/listar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar clientes ativos e retornar 200")
    @WithMockUser(roles = "ADMIN")
    void deveListarClientesAtivosEretornar200() throws Exception{
        var page = new PageImpl<>(List.of(clienteResponse), PageRequest.of(0,10), 1);

        when(clienteService.listarClientesAtivos(any())).thenReturn(page);

        mockMvc.perform(get("/api/clientes/listar")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }

    @Test
    @DisplayName("Deve buscar clientes por ID e retornar 200")
    @WithMockUser(roles = "ADMIN")
    void deveBuscarClientePorIdERetornar200() throws Exception{
       when(clienteService.buscarPorId(1L)).thenReturn(clienteResponse);

       mockMvc.perform(get("/api/clientes/1"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve Retornar 404 ao buscar clientes por ID inexistente")
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404AoBuscarClientePorIdInexistente() throws Exception{
        when(clienteService.buscarPorId(99L)).thenThrow(new EntityNotFoundException("Cliente não localizado."));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar cliente e retonar 200")
    @WithMockUser(roles = "CLIENTE")
    void deveAtualizarClienteComSucesso() throws Exception{
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
    @DisplayName("Deve retornar 404 ao atualizar cliente inexistente")
    @WithMockUser(roles = "CLIENTE")
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception{
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
    @DisplayName("Deve inativar cliente e retornar 200")
    @WithMockUser(roles = "CLIENTE")
    void deveInativarCliente() throws Exception{
        when(clienteService.buscarPorId(1L)).thenReturn(clienteResponse);

        mockMvc.perform(patch("/api/clientes/1/toggle"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Deve retonar 404 ao inativar cliente inexistente")
    @WithMockUser(roles = "CLIENTE")
    void deveRetornar404AoInativarClienteInexistente() throws Exception{
        when(clienteService.buscarPorId(99L)).thenThrow( new EntityNotFoundException("Cliente não encontrado."));

        mockMvc.perform(patch("/api/clientes/99/toggle"))
                .andExpect(status().isOk());
    }
}
