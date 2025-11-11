package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoVehiculo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarVehiculoRequest {

    private String marca;
    private String modelo;
    private String color;
    private EstadoVehiculo estado;
    private Integer cant_puertas;
    private Integer id_motor;
    private Integer id_tipo_vehiculo;
    private Integer id_sucursal;
}
