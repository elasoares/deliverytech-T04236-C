package com.deliverytech.delivery.config;

import com.deliverytech.delivery.dto.response.ClienteDTOResponse;
import com.deliverytech.delivery.dto.response.RestauranteDTOResponse;
import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.model.Restaurante;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(Cliente.class, ClienteDTOResponse.class)
                .addMappings(m -> {
                    m.map(src -> src.getUsuario().getNome(), ClienteDTOResponse::setNome);

                    m.map(src -> src.getUsuario().getEmail(), ClienteDTOResponse::setEmail);
                });

        mapper.typeMap(Restaurante.class, RestauranteDTOResponse.class)
                .addMappings( m -> {
                    m.map(src -> src.getUsuario().getEmail(), RestauranteDTOResponse::setEmail);
                });

        return mapper;
    }
}
