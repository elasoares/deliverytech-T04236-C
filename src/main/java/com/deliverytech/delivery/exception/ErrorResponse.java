package com.deliverytech.delivery.exception;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        long timestamp,
        List<ValidationError> errors) {

            public record ValidationError(
                    String field,
                    String message
            ){}
}
