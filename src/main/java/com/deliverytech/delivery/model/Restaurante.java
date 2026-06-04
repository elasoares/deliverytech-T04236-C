package com.deliverytech.delivery.model;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "restaurantes")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private BigDecimal avaliacao;   
    private boolean ativo;

    @OneToMany(mappedBy = "restaurante", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();
}
