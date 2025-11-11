package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoCombustible {
    NAFTA("Nafta"),
    DIESEL("Diesel"),
    GNC("GNC");
    private final String descripcion;
}
