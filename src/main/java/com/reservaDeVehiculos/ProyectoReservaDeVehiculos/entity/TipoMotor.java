package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoMotor {
    MANUAL("Manual"),
    AUTOMATICO("Automatico"),
    ELECTRICO("Eléctrico"),
    HIBRIDO("Híbrido");
    private final String descripcion;
}
