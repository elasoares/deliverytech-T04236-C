package com.deliverytech.delivery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery.model.Produto;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId, Pageable pageable);

}
