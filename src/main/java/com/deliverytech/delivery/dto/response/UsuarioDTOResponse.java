package com.deliverytech.delivery.dto.response;

import com.deliverytech.delivery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioDTOResponse {
    private Long id;
    private String email;
    private String nome;
    private Role role;
}

