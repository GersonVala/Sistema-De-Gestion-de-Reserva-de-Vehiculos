package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;
import org.springframework.stereotype.Component;

@Component
public class CarStrategy implements VehicleTypeStrategy {

    @Override
    public void validateSpecificAttributes(CrearVehiculoRequest request) {
        if (request.getCant_puertas() == null || request.getCant_puertas() <= 0) {
            throw new IllegalArgumentException("Los autos deben tener un número de puertas válido.");
        }
    }

    @Override
    public boolean supports(String tipoVehiculoNombre) {
        return "AUTO".equalsIgnoreCase(tipoVehiculoNombre) || "CAR".equalsIgnoreCase(tipoVehiculoNombre);
    }
}
