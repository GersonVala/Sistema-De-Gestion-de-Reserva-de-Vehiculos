package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReservaResponse {

    private Integer id_detalle;
    private String observaciones;
    private BigDecimal precio_unitario;
    private Integer id_vehiculo;
    private String vehiculoInfo; // "Toyota Corolla - ABC123"
    private Integer id_reserva;
}
