package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarUsuarioRequest {

    @Size(max = 30, message = "El nombre no puede exceder 30 caracteres")
    private String nombre_usuario;

    @Size(max = 30, message = "El apellido no puede exceder 30 caracteres")
    private String apellido_usuario;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email_usuario;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono_usuario;

    private Integer id_direccion;
}

