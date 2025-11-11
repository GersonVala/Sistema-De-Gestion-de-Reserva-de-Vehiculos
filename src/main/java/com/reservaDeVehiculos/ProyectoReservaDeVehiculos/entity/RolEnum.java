package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolEnum {

    ADMINISTRADOR("Administrador"),
    CLIENTE("Cliente"),
    VENDEDOR("Vendedor");

    private final String descripcion;
}
