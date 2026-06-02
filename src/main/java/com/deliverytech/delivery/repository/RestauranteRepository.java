package com.deliverytech.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{
    
}
