package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.enums.CategoriaRestaurante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.deliverytech.delivery.model.Restaurante;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{
    /*List<Restaurante> findByCategoria(String categoria);*/
    Page<Restaurante> findByAtivoTrue(Pageable pageable);
    /*boolean existsByNome(String nome);*/
    Page<Restaurante> findByCategoriaAndAtivoTrue(CategoriaRestaurante categoria, Pageable pageable);
    boolean existsByUsuario_Id(Long id);
}
