package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un recurso
 * para el cual no tiene permisos (HTTP 403 Forbidden).
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String mensaje) {
        super(mensaje);
    }
    
    public ForbiddenException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
