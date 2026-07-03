package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try{
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            return Health.up()
                    .withDetail("database", "H2")
                    .withDetail("status", "Conexão ativa")
                    .withDetail("query", "SELECT 1 executada com sucesso")
                    .build();

        }catch (Exception e){
            return Health.down()
                    .withDetail("database", "H2")
                    .withDetail("erro", e.getMessage())
                    .build();
        }
    }
}
