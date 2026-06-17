package com.deliverytech.delivery.dto.request.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(T dados, LocalDateTime timestamp) {

    public ApiResponse(T dados){
        this(dados, LocalDateTime.now());
    }

}
