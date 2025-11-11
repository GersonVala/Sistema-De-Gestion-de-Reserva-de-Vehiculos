package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearCiudadRequest {

    @NotBlank(message = "El nombre de la ciudad es obligatorio")
    @Size(max = 30, message = "El nombre de la ciudad no puede exceder 30 caracteres")
    private String nombre_ciudad;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 30, message = "El estado no puede exceder 30 caracteres")
    private String estado;
}

