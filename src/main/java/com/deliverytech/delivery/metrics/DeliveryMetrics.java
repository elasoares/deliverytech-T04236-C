package com.deliverytech.delivery.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DeliveryMetrics  {
    private final MeterRegistry registry;

    private final Counter pedidosPendentes;
    private final Counter pedidosConfirmados;
    private final Counter pedidosCancelados;
    private final Counter pedidosEntregues;

    private final AtomicInteger usuariosAtivos = new AtomicInteger(0);

    private final Counter receitaTotal;
    private final Counter produtosVendidos;


    public DeliveryMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.pedidosPendentes = Counter.builder("delivery.pedidos.total")
                .tag("status", "PENDENTE")
                .description("Total de pedido pendentes")
                .register(registry);
        this.pedidosConfirmados = Counter.builder("delivery.pedidos.total")
                .tag("status", "CONFIRMADO")
                .description("Total de pedido confirmados")
                .register(registry);
        this.pedidosCancelados = Counter.builder("delivery.pedidos.total")
                .tag("status", "CANCELADO")
                .description("Total de pedido cancelado")
                .register(registry);
        this.pedidosEntregues = Counter.builder("delivery.pedidos.total")
                .tag("status", "ENTREGUE")
                .description("Total de pedido entregue")
                .register(registry);
        this.receitaTotal = Counter.builder("delivery.pedidos.total")
                .description("Receita total acumulada em reais")
                .baseUnit("reais")
                .register(registry);
        this.produtosVendidos = Counter.builder("delivery.pedidos.total")
                .description("Total produtos vendidos")
                .register(registry);

        Gauge.builder("delivery.usuarios.ativos", usuariosAtivos, AtomicInteger::get)
                .description("Número de usuários ativos no momento")
                .register(registry);
    }

    public void incrementarPedidosPendentes(){
        pedidosPendentes.increment();
    }

    public void incrementarPedidosConfirmados(){
        pedidosConfirmados.increment();
    }

    public void incrementarPedidosCancelados(){
        pedidosCancelados.increment();
    }

    public void incrementarPedidosEntregues(){
        pedidosEntregues.increment();
    }

    public void adicionarReceita(double valor){
        receitaTotal.increment(valor);
    }

    public void incrementarProdutosVendidos(int quantidade){
        produtosVendidos.increment(quantidade);
    }


    public void incrementarUsuariosAtivos(){
        usuariosAtivos.incrementAndGet();
    }

    public void decrementarUsuariosAtivos(){
        usuariosAtivos.decrementAndGet();
    }

    public Timer.Sample iniciarTimer(){
        return Timer.start(registry);
    }

    public void finalizarTimer(Timer.Sample sample, String operacao, String status){
        sample.stop(Timer.builder("delivery.operacao.latencia")
                .tag("operacao", operacao)
                .tag("status", status)
                .description("Lantência das operações críticas")
                .register(registry));
    }

}
