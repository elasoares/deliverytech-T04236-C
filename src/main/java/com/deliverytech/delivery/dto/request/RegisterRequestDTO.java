package com.deliverytech.delivery.dto.request;

import com.deliverytech.delivery.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String email;
    private String senha;
    private String nome;
    private Role role;
}
