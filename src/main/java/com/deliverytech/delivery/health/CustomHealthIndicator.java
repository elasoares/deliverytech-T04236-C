/*
package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customHealth")
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean serviceOk = false;

        if(serviceOk){
            return Health.up()
                    .withDetail("DeliveryApi", "Funcionando")
                    .build();
        }else{
            return Health.down()
                    .withDetail("DeliveryApi", "Falhou")
                    .build();
        }



    }
}
*/
