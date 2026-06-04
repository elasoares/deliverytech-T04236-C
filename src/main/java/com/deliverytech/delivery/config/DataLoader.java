package com.deliverytech.delivery.config;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(ClienteRepository clienteRepository, RestauranteRepository restauranteRepository, ProdutoRepository produtoRepository){

        return args ->{
            System.out.println("Iniciando carga de dados...");

            System.out.println("Cliente criado: ");

            Cliente c1 = new Cliente();
            c1.setNome("Wilson Martins");
            c1.setEmail("wilson@gmail.com");
            c1.setTelefone("1191111-1111");
            c1.setEndereco("Rua 1, A");
            c1.setAtivo(true);

            clienteRepository.save(c1);

            System.out.println("Usuário cliente " + c1.getNome() + " do e-mail " + c1.getEmail() + " cadastrado com sucesso!");

            System.out.println("Restaurante criado: ");

            Restaurante r1 = new Restaurante();
            r1.setNome("Pizza Top");
            r1.setAtivo(true);
            r1.setEndereco("Rua 2, B");
            r1.setTelefone("1192222-2222");
            r1.setCategoria("Pizzaria");
            r1.setAvaliacao(new BigDecimal(4.5));

            restauranteRepository.save(r1);

            System.out.println("Usuário restaurante " + r1.getNome() + " cadastrado com sucesso!" );
            System.out.println(r1.getCategoria());
            System.out.println(r1.getProdutos());
            System.out.println(r1.getTelefone());


            System.out.println("Produto criado: ");

            Produto p1 = new Produto();
            p1.setNome("Pizza de Calabresa");
            p1.setCategoria("Pizza");
            p1.setDescricao("Pizza de calabresa com queijo");
            p1.setPreco(new BigDecimal("45.00"));
            p1.setRestaurante(r1);

            /*produtoRepository.save(p1);*/

            System.out.println("Produto " + p1.getNome() + " Foi cadastrado com sucesso!");
            System.out.print("Preço: ");
            System.out.println(p1.getPreco());


        };
    }

}
