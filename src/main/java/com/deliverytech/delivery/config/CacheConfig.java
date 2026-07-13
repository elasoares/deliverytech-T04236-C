package com.deliverytech.delivery.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        return new ConcurrentMapCacheManager(
                "restaurantes",
                "restaurante",
                "clientes",
                "cliente",
                "produtoPorRestaurante"

        );
    }
}
