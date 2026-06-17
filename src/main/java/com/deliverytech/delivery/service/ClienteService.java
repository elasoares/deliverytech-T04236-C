package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.response.ClienteDTORequest;
import com.deliverytech.delivery.dto.request.response.ClienteDTOResponse;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ModelMapper mapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper mapper){
        this.repository = clienteRepository;
        this.mapper = mapper;
    }


    public ClienteDTOResponse cadastrarCliente(ClienteDTORequest dto){
        if( repository.existsByEmail(dto.getEmail()) ){
            throw new BusinessException("Email já cadastrado.");
        }

        Cliente novoCliente = mapper.map(dto, Cliente.class);
        novoCliente.setAtivo(true);
        novoCliente.setDataCadastro(LocalDateTime.now());

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

    public ClienteDTOResponse atualizarCliente(Long id, ClienteDTORequest dto){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());
        Cliente salvo = repository.save(cliente);

        return mapper.map(salvo, ClienteDTOResponse.class);

    }

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
