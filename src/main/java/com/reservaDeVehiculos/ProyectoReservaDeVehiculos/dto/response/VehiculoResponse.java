package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponse {

    private Integer id_vehiculo;
    private String marca;
    private String modelo;
    private String patente;
    private String color;
    private EstadoVehiculo estado;
    private Integer cant_puertas;
    private String nombreTipoVehiculo;
    private String descripcionTipoVehiculo;
    private Double cilindradaMotor;
    private Integer caballosDeFuerzaMotor;
    private String tipoCombustible;
    private String tipoMotor;
}

