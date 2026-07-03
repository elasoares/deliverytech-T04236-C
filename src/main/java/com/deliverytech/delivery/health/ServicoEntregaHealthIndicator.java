package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("servicoEntrega")
public class ServicoEntregaHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try{
            boolean servicoDisponivel = verificarServicoEntrega();
            if(servicoDisponivel){
                return Health.up()
                        .withDetail("servico", "Api Entrega")
                        .withDetail("status", "Disponível")
                        .withDetail("latencia", "45ms")
                        .build();
            }else{
                return Health.up()
                        .withDetail("servico", "Api Entrega")
                        .withDetail("status", "Indisponível")
                        .withDetail("motivo", "Timeout na conexão")
                        .build();
            }

        }catch (Exception e ){
            return Health.down()
                    .withDetail("Servico", "Api Entrega")
                    .withDetail("erro", e.getMessage())
                    .build();
        }
    }

    private boolean verificarServicoEntrega(){
        return true;
    }
}
