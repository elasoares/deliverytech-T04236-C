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
    private final Counter pedidosPreparando;
    private final Counter pedidosSaiuParaEntrega;
    private final Counter pedidosFinalizado;

    private final AtomicInteger usuariosAtivos = new AtomicInteger(0);

    private final Counter receitaTotal;
    private final Counter produtosVendidos;


    private final Counter clientesCadastrados;
    private final Counter clientesAtualizado;
    private final Counter clientesInativados;


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
        this.pedidosPreparando = Counter.builder("delivery.pedidos.total")
                .tag("status", "PREPARANDO")
                .register(registry);
        this.pedidosSaiuParaEntrega = Counter.builder("delivery.pedidos.total")
                .tag("status", "SAIU_PARA_ENTREGA")
                .register(registry);
        this.pedidosFinalizado = Counter.builder("delivery.pedidos.total")
                .description("Total de pedidos finalizados com sucesso")
                .register(registry);


        this.receitaTotal = Counter.builder("delivery.pedidos.total")
                .description("Receita total acumulada em reais")
                .baseUnit("reais")
                .register(registry);
        this.produtosVendidos = Counter.builder("delivery.pedidos.total")
                .description("Total produtos vendidos")
                .register(registry);

        this.clientesCadastrados = Counter.builder("delivery.clientes.cadastrados")
                        .description("Total de clientes cadastrados")
                                .register(registry);
        this.clientesAtualizado = Counter.builder("delivery.clientes.atualizados")
                .description("Total de clientes atualizados")
                .register(registry);
        this.clientesInativados = Counter.builder("delivery.clientes.inativados")
                        .description("Total de clientes inativados ou reativados")
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
    public void incrementarPedidosPreparando(){pedidosPreparando.increment();}
    public void incrementarPedidosSaiuParaEntrega(){pedidosSaiuParaEntrega.increment();}
    public void incrementarPedidosFinalizados(){pedidosFinalizado.increment();}



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


    public  void incrementarClientesCadastrados(){
        clientesCadastrados.increment();
    }

    public void incrementarClientesAtualizados(){
        clientesAtualizado.increment();
    }

    public void incrementarClientesInativados(){
        clientesInativados.increment();
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
