package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("espacoDisco")
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long LIMITE_MINIMO_BYTES = 500L * 1024 * 1024;

    @Override
    public Health health() {
        File disco = new File(".");
        long espacoLivre = disco.getFreeSpace();
        long espacoTotal = disco.getTotalSpace();
        long espacoUsado = espacoTotal - espacoLivre;

        double percentualUsado = (double) espacoUsado / espacoTotal * 100;

        if(espacoLivre > LIMITE_MINIMO_BYTES){
            return Health.up()
                    .withDetail("espacoLivre", formatarBytes(espacoLivre))
                    .withDetail("espacoTotal", formatarBytes(espacoTotal))
                    .withDetail("percentualUsado", String.format("%.1f%%", percentualUsado))
                    .withDetail("limiteMinimo", "500MB")
                    .build();
        }else{
            return Health.down()
                    .withDetail("espacoLivre", formatarBytes(espacoLivre))
                    .withDetail("espacoTotal", formatarBytes(espacoTotal))
                    .withDetail("percentualUsado", String.format("%.1f%%", percentualUsado))
                    .withDetail("motivo", "Espaço em disco abaixo do limite mínimo")
                    .build();
        }


    }


    private String formatarBytes(long bytes){
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", gb);
    }
}

