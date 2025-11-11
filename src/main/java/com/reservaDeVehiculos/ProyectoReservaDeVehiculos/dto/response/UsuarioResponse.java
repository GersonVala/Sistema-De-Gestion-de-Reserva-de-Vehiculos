    private String email_usuario;
    private String dni_usuario;
    private String telefono_usuario;
    private DireccionResponse direccion;
    private List<String> roles;
}
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
public class UsuarioResponse {

    private Integer id_usuario;
    private String nombre_usuario;
    private String apellido_usuario;

