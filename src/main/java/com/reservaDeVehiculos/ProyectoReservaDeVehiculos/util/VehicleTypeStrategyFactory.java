package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class VehicleTypeStrategyFactory {

    private final Map<String, VehicleTypeStrategy> strategies;

    @Autowired
    public VehicleTypeStrategyFactory(List<VehicleTypeStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName().replace("Strategy", "").toLowerCase(),
                        Function.identity()
                ));
    }

    public VehicleTypeStrategy getStrategy(String tipoVehiculoNombre) {
        return strategies.values().stream()
                .filter(strategy -> strategy.supports(tipoVehiculoNombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró estrategia para el tipo de vehículo: " + tipoVehiculoNombre));
    }
}
