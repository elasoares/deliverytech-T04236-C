package com.deliverytech.delivery.model;

import com.deliverytech.delivery.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="data_pedido")
    private LocalDateTime dataPedido;

    @Column(name="endereco_entrega")
    private String enderecoEntrega;

    @Column(name="valor_total")
    private BigDecimal valorTotal;

    @Column(name="taxa_entrega")
    private BigDecimal taxaEntrega;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.dataPedido = LocalDateTime.now();
    }
}
