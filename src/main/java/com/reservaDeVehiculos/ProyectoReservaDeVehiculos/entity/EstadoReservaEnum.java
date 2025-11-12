package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoReservaEnum {

    PENDIENTE("Pendiente de aprobación"),      // Cliente solicitó, esperando vendedor
    CONFIRMADA("Confirmada"),                  // Vendedor aprobó, esperando retiro
    RECHAZADA("Rechazada"),                    // Vendedor rechazó la solicitud
    ALQUILADO("En alquiler"),                  // Cliente retiró el vehículo
    COMPLETADA("Completada"),                  // Cliente devolvió el vehículo
    CANCELADA("Cancelada");                    // Cliente canceló antes de confirmar

    private final String descripcion;
}
