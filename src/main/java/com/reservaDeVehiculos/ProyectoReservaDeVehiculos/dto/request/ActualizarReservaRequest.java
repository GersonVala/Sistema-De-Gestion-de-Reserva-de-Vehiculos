package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarReservaRequest {

    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private EstadoReservaEnum estado;
    private BigDecimal precio_reserva;
    private Integer id_sucursal;
}

