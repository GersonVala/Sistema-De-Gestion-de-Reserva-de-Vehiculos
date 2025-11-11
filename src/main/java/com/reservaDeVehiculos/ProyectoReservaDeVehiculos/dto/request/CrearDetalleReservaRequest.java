package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CrearDetalleReservaRequest {

    @NotBlank(message = "Las observaciones son obligatorias")
    @Size(max = 255, message = "Las observaciones no pueden exceder 255 caracteres")
    private String observaciones;

    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precio_unitario;

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Integer id_vehiculo;

    @NotNull(message = "El ID de la reserva es obligatorio")
    private Integer id_reserva;
}
