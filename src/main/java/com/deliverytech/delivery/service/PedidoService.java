package com.deliverytech.delivery.service;

import java.math.BigDecimal;

import com.deliverytech.delivery.metrics.DeliveryMetrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery.dto.request.ItemPedidoDTO;
import com.deliverytech.delivery.dto.request.PedidoDTO;
import com.deliverytech.delivery.dto.response.PedidoDTOResponse;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.model.ItemPedido;
import com.deliverytech.delivery.model.Pedido;
import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.model.Usuario;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final ModelMapper mapper;
    private final DeliveryMetrics metrics;
    private final Tracer tracer;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            RestauranteRepository restauranteRepository,
            ProdutoRepository produtoRepository,
            ModelMapper mapper, DeliveryMetrics metrics, Tracer tracer) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.mapper = mapper;
        this.metrics = metrics;
        this.tracer = tracer;
    }

    private PedidoDTOResponse toDTO(Pedido pedido) {
        return mapper.map(pedido, PedidoDTOResponse.class);
    }


    @Transactional
    public PedidoDTOResponse criarPedido(PedidoDTO dto, Usuario usuarioLogado) {
        Timer.Sample timer = metrics.iniciarTimer();
        Span span = tracer.nextSpan().name("criar-pedido").start();
        try(var ws = tracer.withSpan(span)){
            if (usuarioLogado == null) {
                throw new BusinessException("Usuário não autenticado.");
            }
            Cliente cliente = clienteRepository.findByUsuario_Id(usuarioLogado.getId())
                    .orElseThrow(() -> new BusinessException("Cliente não encontrado para este usuário."));

            if (!cliente.isAtivo()) {
                throw new BusinessException("Cliente inativo.");
            }

            Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setEnderecoEntrega(dto.getEnderecoEntrega());

            BigDecimal total = BigDecimal.ZERO;

            for (ItemPedidoDTO itemDTO : dto.getItens()) {

                Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

                if (!produto.isDisponivel()) {
                    throw new BusinessException("Produto indisponível: " + produto.getNome());
                }

                ItemPedido item = new ItemPedido();
                item.setPedido(pedido);
                item.setProduto(produto);
                item.setQuantidade(itemDTO.getQuantidade());
                item.setPrecoUnitario(produto.getPreco());

                BigDecimal subtotal = produto.getPreco()
                        .multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

                item.setSubtotal(subtotal);

                pedido.getItens().add(item);
                total = total.add(subtotal);

                metrics.incrementarProdutosVendidos(itemDTO.getQuantidade());
            }

            pedido.setValorTotal(total);

            PedidoDTOResponse response =  toDTO(pedidoRepository.save(pedido));

            metrics.incrementarPedidosPendentes();
            metrics.adicionarReceita(total.doubleValue());
            metrics.finalizarTimer(timer, "criar_pedido", "sucesso");


            span.tag("usuario", usuarioLogado.getEmail());
            span.tag("restaurante", dto.getRestauranteId().toString());

            span.tag("total", total.toString());

            return response;
        }catch (Exception e){
            metrics.finalizarTimer(timer, "criar_pedido", "erro");
            span.error(e);
            throw  e;
        }finally {
            span.end();
        }
    }

    private void validarDonoPedido(Pedido pedido, Usuario usuarioLogado) {

        if (!pedido.getCliente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não tem permissão para acessar este pedido.");
        }
    }

    @Transactional
    public PedidoDTOResponse cancelarPedido(Long pedidoId, Usuario usuarioLogado) {
        Timer.Sample timer = metrics.iniciarTimer();
        try{
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

            validarDonoPedido(pedido, usuarioLogado);

            if (pedido.getStatus() == StatusPedido.ENTREGUE) {
                throw new BusinessException("Pedido entregue não pode ser cancelado.");
            }

            pedido.setStatus(StatusPedido.CANCELADO);

            PedidoDTOResponse response =  toDTO(pedidoRepository.save(pedido));
            metrics.incrementarPedidosCancelados();
            metrics.finalizarTimer(timer, "cancelar_pedido", "sucesso");
            return response;
        }catch (Exception e){
            metrics.finalizarTimer(timer, "cancelar_pedido", "erro");
            throw  e;
        }
    }

    @Transactional
    public Page<PedidoDTOResponse> meusPedidos(Usuario usuarioLogado, Pageable pageable) {

        Cliente cliente = clienteRepository.findByUsuario_Id(usuarioLogado.getId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado."));

        return pedidoRepository.buscarItensPorClientes(cliente.getId(), pageable)
                .map(this::toDTO);
    }

    @Transactional
    public PedidoDTOResponse atualizarStatus(Long pedidoId, StatusPedido novoStatus, Usuario usuarioLogado){
        Timer.Sample timer = metrics.iniciarTimer();
        try{
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(()-> new EntityNotFoundException("Pedido não encontrado."));

            validarTransicaoStatus(pedido.getStatus(), novoStatus);

            StatusPedido statusAnterior = pedido.getStatus();
            pedido.setStatus(novoStatus);
            PedidoDTOResponse reponse = toDTO(pedidoRepository.save(pedido));

            registrarMetricasStatus(novoStatus, statusAnterior);

            metrics.finalizarTimer(timer, "atualizar_status_pedido", "sucesso");
            return reponse;

        }catch (Exception e){
            metrics.finalizarTimer(timer, "atualizar_status_pedido", "erro");
            throw e;
        }
    }

    private void validarTransicaoStatus(StatusPedido atual, StatusPedido novo){
        switch (atual){
            case PENDENTE -> {
                if(novo != StatusPedido.CONFIRMADO && novo != StatusPedido.CANCELADO){
                    throw new BusinessException("Pedido pendente só pode ir para CONFIRMADO ou CANCELADO.");
                }
            }
            case CONFIRMADO ->{
                if(novo != StatusPedido.PREPARANDO && novo != StatusPedido.CANCELADO){
                    throw new BusinessException("Pedido confirmado só pode ir para PREPARANDO ou CANCELADO.");
                }
            }
            case PREPARANDO ->{
                if(novo != StatusPedido.SAIU_PARA_ENTREGA){
                    throw new BusinessException("Pedido em preparo só pode ir para SAIU_PARA_ENTREGA");
                }
            }
            case SAIU_PARA_ENTREGA ->{
                if(novo != StatusPedido.ENTREGUE){
                    throw  new BusinessException("Pedido saiu para entrega só pode ir para ENTREGUE.");
                }
            }
            case ENTREGUE, CANCELADO -> {
                throw new BusinessException("Pedido " + atual + " não pode mais ser alterado.");
            }
        }
    }

    private void registrarMetricasStatus(StatusPedido novo, StatusPedido anterior){
        switch (novo){
            case CONFIRMADO -> metrics.incrementarPedidosConfirmados();
            case CANCELADO -> metrics.incrementarPedidosCancelados();
            case PREPARANDO -> metrics.incrementarPedidosPreparando();
            case SAIU_PARA_ENTREGA -> metrics.incrementarPedidosSaiuParaEntrega();
            case ENTREGUE -> {
                metrics.incrementarPedidosEntregues();
                metrics.incrementarPedidosFinalizados();
            }

        }
    }

}