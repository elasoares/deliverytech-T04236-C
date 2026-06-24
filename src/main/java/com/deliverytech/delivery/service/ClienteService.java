package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ClienteDTO;
import com.deliverytech.delivery.dto.request.ClienteDTOAtualizar;
import com.deliverytech.delivery.dto.response.ClienteDTOResponse;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ModelMapper mapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper mapper){
        this.repository = clienteRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ClienteDTOResponse cadastrarCliente(ClienteDTO dto, Usuario usuarioLogado){
        if(usuarioLogado == null){
            throw new BusinessException("Usuário não autenticado.");
        }

        if(!usuarioLogado.getRole().name().equals("CLIENTE")
                && !usuarioLogado.getRole().name().equals("ADMIN")){
            throw new BusinessException("Apenas CLIENTE ou ADMIN podem criar perfil de cliente.");
        }

        if(repository.existsByUsuario_Id(usuarioLogado.getId())){
            throw new BusinessException("Cliente já cadastrado para esse usuário.");
        }

        Cliente novoCliente = mapper.map(dto, Cliente.class);
        novoCliente.setUsuario(usuarioLogado);
        novoCliente.setNome(usuarioLogado.getNome());
        novoCliente.setEmail(usuarioLogado.getEmail());
        novoCliente.setAtivo(true);

        Cliente salvo = repository.save(novoCliente);

        return mapper.map(salvo, ClienteDTOResponse.class);

    }

    public ClienteDTOResponse buscarPorId(Long id){
        Cliente cliente = repository.findById(id)
                .orElseThrow( () ->
                        new EntityNotFoundException("Cliente não encontrado."));
        return mapper.map(cliente, ClienteDTOResponse.class);
    }

    public ClienteDTOResponse buscarPorEmail(String email){
        Cliente emailCliente = repository.findByEmail(email)
                .orElseThrow(()->
                new EntityNotFoundException("E-mail do cliente não localizado.")
                );
        return mapper.map(emailCliente, ClienteDTOResponse.class);

    }

    @Transactional
    public ClienteDTOResponse atualizarCliente(Long id, ClienteDTOAtualizar dto){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());
        Cliente salvo = repository.save(cliente);

        return mapper.map(salvo, ClienteDTOResponse.class);

    }

    @Transactional
    public ClienteDTOResponse toggle(Long id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Cliente não encontrado."));
        cliente.setAtivo(!cliente.isAtivo());

        Cliente salvo = repository.save(cliente);

        return mapper.map(salvo, ClienteDTOResponse.class);
    }

    public Page<ClienteDTOResponse> listarClientesAtivos(Pageable pageable){
       return  repository.findByAtivoTrue(pageable).map(c -> mapper.map(c, ClienteDTOResponse.class));
    }




}
