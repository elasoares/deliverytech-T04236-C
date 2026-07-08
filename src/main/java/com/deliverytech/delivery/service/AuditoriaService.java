package com.deliverytech.delivery.service;

import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {
    private static final Logger audiLog = LoggerFactory.getLogger("AUDITORIA");

    /**
     * @param acao
     * @param usuario
     * @param recurso
     * @param detalhe
     */
    public void registrar(String acao, String usuario, String recurso, String detalhe){
        audiLog.info("Evento de auditoria",
                StructuredArguments.keyValue("acao", acao),
                StructuredArguments.keyValue("usuario", usuario),
                StructuredArguments.keyValue("recurso", recurso),
                StructuredArguments.keyValue("detalhe", detalhe));
    }
}
