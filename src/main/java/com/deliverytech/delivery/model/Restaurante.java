package com.deliverytech.delivery.model;

import com.deliverytech.delivery.enums.CategoriaRestaurante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Enumerated(EnumType.STRING)
    private CategoriaRestaurante categoria;

    private String endereco;
    private String telefone;
    private BigDecimal avaliacao;
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "restaurante", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();


}
