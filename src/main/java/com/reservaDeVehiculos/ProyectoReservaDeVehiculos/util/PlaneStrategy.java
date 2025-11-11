package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;
import org.springframework.stereotype.Component;

@Component
public class PlaneStrategy implements VehicleTypeStrategy {

    @Override
    public void validateSpecificAttributes(CrearVehiculoRequest request) {
        // Los aviones no requieren puertas en el sentido tradicional, pero podríamos validar capacidad de pasajeros, etc.
        // Por simplicidad, no validamos nada específico aquí.
    }

    @Override
    public boolean supports(String tipoVehiculoNombre) {
        return "AVION".equalsIgnoreCase(tipoVehiculoNombre) || "PLANE".equalsIgnoreCase(tipoVehiculoNombre);
    }
}
