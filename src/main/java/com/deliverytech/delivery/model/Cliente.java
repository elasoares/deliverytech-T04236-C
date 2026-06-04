package com.deliverytech.delivery.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    private boolean ativo;

    @PrePersist
    public void prePersist(){
        this.dataCadastro = LocalDateTime.now();
    }
}
