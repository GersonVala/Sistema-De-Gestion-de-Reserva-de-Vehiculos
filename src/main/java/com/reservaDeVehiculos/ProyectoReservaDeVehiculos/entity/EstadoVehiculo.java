package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoVehiculo {

    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    ENTREGADO("Entregado"),
    DESCOMPUESTO("Descompuesto"),
    EN_MANTENIMIENTO("En Mantenimiento");

    private final String descripcion;
}
