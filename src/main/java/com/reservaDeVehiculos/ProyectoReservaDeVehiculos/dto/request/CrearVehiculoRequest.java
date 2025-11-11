package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearVehiculoRequest {

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "La patente es obligatoria")
    private String patente;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;

    @NotNull(message = "La cantidad de puertas es obligatoria")
    @Positive(message = "La cantidad de puertas debe ser positiva")
    private Integer cant_puertas;

    @NotNull(message = "El ID del motor es obligatorio")
    private Integer id_motor;

    @NotNull(message = "El ID del tipo de vehículo es obligatorio")
    private Integer id_tipo_vehiculo;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Integer id_sucursal;
}
