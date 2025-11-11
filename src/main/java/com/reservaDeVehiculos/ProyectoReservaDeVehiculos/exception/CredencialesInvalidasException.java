package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}

