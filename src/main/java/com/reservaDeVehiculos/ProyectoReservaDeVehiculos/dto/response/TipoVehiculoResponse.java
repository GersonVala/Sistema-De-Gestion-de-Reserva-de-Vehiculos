package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoVehiculoResponse {

    private Integer id_tipo_vehiculo;
    private String nombre_vehiculo;
    private String descripcion_vehiculo;
}

