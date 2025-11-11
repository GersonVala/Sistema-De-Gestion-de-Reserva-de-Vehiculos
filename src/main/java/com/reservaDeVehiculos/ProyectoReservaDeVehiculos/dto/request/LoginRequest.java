package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email_usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}

