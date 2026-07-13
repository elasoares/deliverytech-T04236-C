package com.deliverytech.delivery.service;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.ProdutoDTO;
import com.deliverytech.delivery.dto.response.ProdutoDTOResponse;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;

import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ModelMapper mapper;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            RestauranteRepository restauranteRepository,
            ModelMapper mapper) {

        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    private ProdutoDTOResponse returnResponseDTO(Produto p) {
        ProdutoDTOResponse dto = mapper.map(p, ProdutoDTOResponse.class);

        if (p.getRestaurante() != null) {
            dto.setRestauranteId(p.getRestaurante().getId());
        }

        return dto;
    }

    @CacheEvict(value = "produtoPorRestaurante", allEntries = true )
    @Transactional
    public ProdutoDTOResponse cadastrar(
            Long restauranteId,
            ProdutoDTO dto,
            Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        boolean isRestaurante = usuarioLogado.getRole().name().equals("RESTAURANTE");
        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");

        if (!isRestaurante && !isAdmin) {
            throw new BusinessException("Acesso negado.");
        }

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));


        if (isRestaurante &&
                !restaurante.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você só pode cadastrar produtos no seu restaurante.");
        }

        Produto produto = mapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        return returnResponseDTO(produtoRepository.save(produto));
    }

    @Cacheable(value = "produtoPorRestaurante",  key="#restauranteId")
    public Page<ProdutoDTOResponse> listarPorRestaurante(Long restauranteId, Pageable pageable) {

        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não localizado.");
        }

        return produtoRepository
                .findByRestauranteIdAndDisponivelTrue(restauranteId, pageable)
                .map(this::returnResponseDTO);
    }

    public ProdutoDTOResponse buscarPorId(Long id) {
        Produto p = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        return returnResponseDTO(p);
    }

    @CacheEvict(value = "produtoPorRestaurante", allEntries = true )
    @Transactional
    public ProdutoDTOResponse toggleDisponibilidade(Long produtoId, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        boolean isRestaurante = usuarioLogado.getRole().name().equals("RESTAURANTE");
        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");

        if (!isRestaurante && !isAdmin) {
            throw new BusinessException("Acesso negado.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (isRestaurante &&
                !produto.getRestaurante().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não pode alterar produto de outro restaurante.");
        }

        produto.setDisponivel(!produto.isDisponivel());

        return returnResponseDTO(produtoRepository.save(produto));
    }
}