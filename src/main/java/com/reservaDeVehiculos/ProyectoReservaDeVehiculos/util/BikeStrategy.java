package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;
import org.springframework.stereotype.Component;

@Component
public class BikeStrategy implements VehicleTypeStrategy {

    @Override
    public void validateSpecificAttributes(CrearVehiculoRequest request) {
        // Las motos no requieren puertas, pero podríamos validar otros atributos si es necesario
        // Por ejemplo, cilindrada, pero como no está en el request, lo dejamos simple
    }

    @Override
    public boolean supports(String tipoVehiculoNombre) {
        return "MOTO".equalsIgnoreCase(tipoVehiculoNombre) || "BIKE".equalsIgnoreCase(tipoVehiculoNombre) || "BICICLETA".equalsIgnoreCase(tipoVehiculoNombre);
    }
}
