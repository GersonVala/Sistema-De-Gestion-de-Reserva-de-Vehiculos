package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoReservaEnum {

    ENTREGADO("Entregado"),
    RESERVADO("Reservado"),
    DISPONIBLE("Disponible");

    private final String descripcion;
}
