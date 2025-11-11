package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Integer id_usuario;
    private String nombre_completo;
    private String email_usuario;
    private String token;
    private String mensaje;
}

