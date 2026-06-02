package com.deliverytech.delivery.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean disponivel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;
}
