package com.deliverytech.delivery.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    @Query(value = """
        SELECT DISTINCT p FROM Pedido p
        JOIN FETCH p.cliente
        JOIN FETCH p.restaurante
        LEFT JOIN FETCH p.itens i
        LEFT JOIN FETCH i.produto
        WHERE p.cliente.id = :clienteId
        """,
            countQuery = "SELECT count(p) FROM Pedido p WHERE p.cliente.id = :clienteId")
    Page<Pedido> buscarItensPorClientes(@Param("clienteId") Long clienteId, Pageable pageable);





}

