package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<String> roles;
    
    // Constructor para compatibilidad con código existente
    public LoginResponse(Integer id_usuario, String nombre_completo, String email_usuario, String token, String mensaje) {
        this.id_usuario = id_usuario;
        this.nombre_completo = nombre_completo;
        this.email_usuario = email_usuario;
        this.token = token;
        this.mensaje = mensaje;
        this.roles = List.of();
    }
}

