package com.deliverytech.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery.model.Produto;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByRestauranteId(Long id);
    /*boolean findByDisponivelTrue();*/
    List<Produto> findByDisponivelTrue();
    List<Produto> findByCategoria(String categoria);
}
