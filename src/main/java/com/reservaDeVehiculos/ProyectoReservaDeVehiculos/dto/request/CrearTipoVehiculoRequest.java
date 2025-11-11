package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearTipoVehiculoRequest {

    @NotBlank(message = "El nombre del vehículo es obligatorio")
    @Size(max = 30, message = "El nombre del vehículo no puede exceder 30 caracteres")
    private String nombre_vehiculo;

    @NotBlank(message = "La descripción del vehículo es obligatoria")
    @Size(max = 50, message = "La descripción del vehículo no puede exceder 50 caracteres")
    private String descripcion_vehiculo;
}

