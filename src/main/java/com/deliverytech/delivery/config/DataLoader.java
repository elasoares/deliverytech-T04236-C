package com.deliverytech.delivery.config;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(ClienteRepository clienteRepository){

        return args ->{
            System.out.println("Iniciando carga de dados...");

            Cliente c1 = new Cliente();
            c1.setNome("Wilson Martins");
            c1.setEmail("wilson@gmail.com");
            c1.setTelefone("1191111-1111");
            c1.setEndereco("Rua 1, A");
            c1.setAtivo(true);

            clienteRepository.save(c1);

            System.out.println("Usuário " + c1.getNome() + "do e-mail " + c1.getEmail() + "cadastrado com sucesso!");

            System.out.println("Nome:");
            System.out.println(c1.getNome());

            System.out.print("Data cadastro:");
            System.out.print(c1.getDataCadastro());
        };
    }

}
