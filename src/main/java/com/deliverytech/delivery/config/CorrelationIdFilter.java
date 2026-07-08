package com.deliverytech.delivery.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID = "correlationId";
    private static final String HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
        try{
            String correlationId = request.getHeader(HEADER);
            if(correlationId == null || correlationId.isBlank()){
                correlationId = UUID.randomUUID().toString();
            }

            MDC.put(CORRELATION_ID, correlationId);
            response.setHeader(HEADER, correlationId);
            chain.doFilter(request, response);
        }finally {
            MDC.clear();
        }
    }
}
