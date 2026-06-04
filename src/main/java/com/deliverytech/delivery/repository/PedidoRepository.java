package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByStatus(StatusPedido status);

}
