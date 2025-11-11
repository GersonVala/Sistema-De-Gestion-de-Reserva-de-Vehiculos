package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CrearReservaRequest {

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser presente o futura")
    private LocalDate fecha_inicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fecha_fin;

    @NotNull(message = "El estado es obligatorio")
    private EstadoReservaEnum estado;

    @NotNull(message = "El precio de reserva es obligatorio")
    private BigDecimal precio_reserva;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer id_usuario;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Integer id_sucursal;
}

