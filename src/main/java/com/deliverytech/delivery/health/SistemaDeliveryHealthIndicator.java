package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class SistemaDeliveryHealthIndicator implements HealthIndicator {
    private final DatabaseHealthIndicator databaseHealth;
    private final ServicoEntregaHealthIndicator servicoEntregaHealth;
    private final DiskSpaceHealthIndicator diskSpaceHealth;

    public SistemaDeliveryHealthIndicator(DatabaseHealthIndicator databaseHealth, ServicoEntregaHealthIndicator servicoEntregaHealth, DiskSpaceHealthIndicator diskSpaceHealth) {
        this.databaseHealth = databaseHealth;
        this.servicoEntregaHealth = servicoEntregaHealth;
        this.diskSpaceHealth = diskSpaceHealth;
    }


    @Override
    public Health health() {
        Health db = databaseHealth.health();
        Health entrega = servicoEntregaHealth.health();
        Health disco = diskSpaceHealth.health();

        boolean tudoOk = db.getStatus().getCode().equals("UP")
                && entrega.getStatus().getCode().equals("UP")
                && disco.getStatus().getCode().equals("UP");

        if(tudoOk){
            return Health.up()
                    .withDetail("banco", db.getStatus().getCode())
                    .withDetail("servicoEntrega", entrega.getStatus().getCode())
                    .withDetail("disco", disco.getStatus().getCode())
                    .withDetail("resumo", "Todos os componentes operacionais")
                    .build();
        }else {
            return Health.down()
                    .withDetail("banco", db.getStatus().getCode())
                    .withDetail("servicoEntrega", entrega.getStatus().getCode())
                    .withDetail("disco", disco.getStatus().getCode())
                    .withDetail("resumo", "Um ou mais componentes com problema")
                    .build();
        }

    }
}
