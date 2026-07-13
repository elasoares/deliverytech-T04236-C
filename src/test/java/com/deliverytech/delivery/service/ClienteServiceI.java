package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ClienteDTO;
import com.deliverytech.delivery.dto.request.ClienteDTOAtualizar;
import com.deliverytech.delivery.dto.response.ClienteDTOResponse;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.metrics.DeliveryMetrics;
import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.ClienteRepository;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceI {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private DeliveryMetrics metrics;

    @InjectMocks
    private ClienteService service;

    private Usuario usuarioCliente;
    private Usuario usuarioAdmin;
    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private ClienteDTOResponse clienteDTOResponse;

    @BeforeEach
    void setUp() {
        Timer.Sample timerSample = mock(Timer.Sample.class);
        lenient().when(metrics.iniciarTimer()).thenReturn(timerSample);

        usuarioCliente = new Usuario();
        usuarioCliente.setId(1L);
        usuarioCliente.setNome("João Pedro");
        usuarioCliente.setEmail("jp@gmail.com");
        usuarioCliente.setRole(Role.CLIENTE);

        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(2L);
        usuarioAdmin.setRole(Role.ADMIN);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setTelefone("(11)91234-1234");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setAtivo(true);
        cliente.setUsuario(usuarioCliente);

        clienteDTO = new ClienteDTO();
        clienteDTO.setTelefone("(11)91234-1234");
        clienteDTO.setEndereco("Rua Teste, 123");

        clienteDTOResponse = new ClienteDTOResponse();
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        when(repository.existsByUsuario_Id(usuarioCliente.getId())).thenReturn(false);
        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(repository.save(any(Cliente.class))).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        ClienteDTOResponse resultado = service.cadastrarCliente(clienteDTO, usuarioCliente);

        assertThat(resultado).isNotNull();
        verify(repository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente já está cadastrado")
    void deveLancarExcecaoQuandoClienteJaCadastrado() {
        when(repository.existsByUsuario_Id(usuarioCliente.getId())).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarCliente(clienteDTO, usuarioCliente))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cliente já cadastrado para esse usuário.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não estiver autenticado")
    void deveLancarExcecaoQuandousuarioNaoEstiverAutenticado() {
        assertThatThrownBy(() -> service.cadastrarCliente(clienteDTO, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuário não autenticado.");
    }

    @Test
    @DisplayName("Deve atualizar dados do cliente com sucesso")
    void deveAtualizarDadosDoClienteComSucesso() {
        ClienteDTOAtualizar dto = new ClienteDTOAtualizar();
        dto.setNome("Novo nome");
        dto.setEmail("novo@gmail.com");
        dto.setTelefone("(11)91234-1234");
        dto.setEndereco("Nova Rua, 456");

        when(repository.findByUsuario_Id(usuarioCliente.getId())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        ClienteDTOResponse resultado = service.atualizarCliente(usuarioCliente, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(repository.findByUsuario_Id(usuarioCliente.getId())).thenReturn(Optional.empty());
        ClienteDTOAtualizar dto = new ClienteDTOAtualizar();

        assertThatThrownBy(() -> service.atualizarCliente(usuarioCliente, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado.");
    }

    @Test
    @DisplayName("Deve inativar cliente ativo")
    void deveInativarClienteAtivo() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        service.toggle(1L);

        assertThat(cliente.isAtivo()).isFalse();
        verify(repository).save(cliente);
    }

    @Test
    @DisplayName("Deve reativar cliente inativado")
    void deveReativarClienteInativar() {
        cliente.setAtivo(false);

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        service.toggle(1L);

        assertThat(cliente.isAtivo()).isTrue();
        verify(repository).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar ID inexistente")
    void deveLancarExcecaoAoInativarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggle(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado.");
    }

    @Test
    @DisplayName("Deve retornar páginas de clientes ativos")
    void deveRetornarPaginasDeClientesAtivos() {
        Page<Cliente> page = new PageImpl<>(List.of(cliente), PageRequest.of(0, 10), 1);

        when(repository.findByAtivoTrue(any())).thenReturn(page);
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        Page<ClienteDTOResponse> resultado = service.listarClientesAtivos(PageRequest.of(0, 10));

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar cliente ao buscar por ID existente")
    void deveRetornarClienteAoBuscarPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.map(cliente, ClienteDTOResponse.class)).thenReturn(clienteDTOResponse);

        ClienteDTOResponse retorno = service.buscarPorId(1L);

        assertThat(retorno).isNotNull();
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por ID inexistente")
    void deveLancarExcecaoAoBuscarClientePorIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado.");
    }
}