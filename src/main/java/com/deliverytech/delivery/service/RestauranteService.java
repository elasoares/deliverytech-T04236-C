package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.RestauranteDTO;
import com.deliverytech.delivery.dto.response.RestauranteDTOResponse;
import com.deliverytech.delivery.enums.CategoriaRestaurante;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RestauranteService {

    private final RestauranteRepository repository;
    private final ModelMapper mapper;

    public RestauranteService(RestauranteRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RestauranteDTOResponse cadastrar(RestauranteDTO dto, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        if(usuarioLogado.getRole().name().equals("RESTAURANTE")){
            if(repository.existsByUsuario_Id(usuarioLogado.getId())){
                throw new BusinessException("Você já possui um restaurante.");
            }
        }

        CategoriaRestaurante categoriaRestaurante = CategoriaRestaurante.valueOf(dto.getCategoria().toUpperCase());
        Restaurante r = mapper.map(dto, Restaurante.class);
        r.setUsuario(usuarioLogado);
        r.setNome(usuarioLogado.getNome());
        r.setCategoria(categoriaRestaurante);
        r.setAtivo(true);
        r.setAvaliacao(BigDecimal.ZERO);

        Restaurante salvo = repository.save(r);
        return mapper.map(salvo, RestauranteDTOResponse.class);
    }

    public Page<RestauranteDTOResponse> listarAtivos(Pageable pageable) {
        return repository.findByAtivoTrue(pageable)
                .map(r -> mapper.map(r, RestauranteDTOResponse.class));
    }

    public Page<RestauranteDTOResponse> buscarPorCategoria(String categoria, Pageable pageable) {
        CategoriaRestaurante categoriaEnum;
        try{
            categoriaEnum = CategoriaRestaurante.valueOf(categoria.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BusinessException("Categoria inválida.");
        }

        return repository.findByCategoriaAndAtivoTrue(categoriaEnum, pageable)
                .map(r -> mapper.map(r, RestauranteDTOResponse.class));
    }

    public RestauranteDTOResponse buscarPorId(Long id) {
        Restaurante r = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));
        return mapper.map(r, RestauranteDTOResponse.class);
    }

    @Transactional
    public RestauranteDTOResponse toggle(Long id) {
        Restaurante restaurante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        restaurante.setAtivo(!restaurante.isAtivo());
        return mapper.map(restaurante, RestauranteDTOResponse.class);
    }
}