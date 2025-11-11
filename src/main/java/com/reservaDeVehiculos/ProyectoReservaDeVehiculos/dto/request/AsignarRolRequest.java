package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsignarRolRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer id_usuario;

    @NotNull(message = "El ID del rol es obligatorio")
    private Integer id_rol;
}

