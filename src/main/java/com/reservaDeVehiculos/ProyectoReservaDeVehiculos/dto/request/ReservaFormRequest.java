package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO simplificado para el formulario HTML de reservas.
 * Captura solo los datos esenciales que el usuario ingresa en el formulario.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReservaFormRequest {

    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private Integer id_sucursal; // Sucursal de retiro
    private Integer id_sucursal_devolucion; // Sucursal de devolución
    private Integer id_vehiculo;
    private Integer id_usuario;
}
