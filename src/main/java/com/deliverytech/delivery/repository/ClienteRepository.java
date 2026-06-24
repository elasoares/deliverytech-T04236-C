package com.deliverytech.delivery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery.model.Cliente;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Page<Cliente> findByAtivoTrue(Pageable pageable);
    /*List<Cliente> findByNomeContainingIgnoreCase(String nome);*/
    boolean existsByEmail(String email);
    boolean existsByUsuario_Id(Long id);
}
