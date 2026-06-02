package com.deliverytech.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}