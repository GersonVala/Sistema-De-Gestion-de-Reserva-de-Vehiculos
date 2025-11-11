package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;

public interface VehicleTypeStrategy {

    /**
     * Valida los atributos específicos del tipo de vehículo.
     * @param request La solicitud de creación del vehículo.
     * @throws IllegalArgumentException si los atributos no son válidos.
     */
    void validateSpecificAttributes(CrearVehiculoRequest request);

    /**
     * Indica si este strategy maneja el tipo de vehículo dado.
     * @param tipoVehiculoNombre El nombre del tipo de vehículo.
     * @return true si maneja este tipo.
     */
    boolean supports(String tipoVehiculoNombre);
}
